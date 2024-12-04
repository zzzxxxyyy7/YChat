package com.ychat.common.Chat.Services.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.ychat.common.Chat.Enum.GroupErrorEnum;
import com.ychat.common.Chat.Enum.GroupRoleAPPEnum;
import com.ychat.common.Chat.Enum.GroupRoleEnum;
import com.ychat.common.Chat.Event.GroupMemberAddEvent;
import com.ychat.common.Chat.Services.ChatService;
import com.ychat.common.Chat.Services.RoomAppService;
import com.ychat.common.Chat.Services.adapter.ChatAdapter;
import com.ychat.common.Chat.Services.adapter.MemberAdapter;
import com.ychat.common.Chat.Services.adapter.RoomAdapter;
import com.ychat.common.Chat.Services.cache.*;
import com.ychat.common.Chat.Services.factory.MsgHandlerFactory;
import com.ychat.common.Chat.Services.handler.AbstractMsgHandler;
import com.ychat.common.Chat.Services.mark.AbstractMsgMarkStrategy;
import com.ychat.common.Chat.Services.mark.MsgMarkFactory;
import com.ychat.common.Chat.domain.dto.*;
import com.ychat.common.Chat.domain.vo.ChatMemberListResp;
import com.ychat.common.Chat.domain.vo.ChatRoomResp;
import com.ychat.common.Chat.domain.vo.MemberResp;
import com.ychat.common.Constants.Enums.Impl.MessageMarkActTypeEnum;
import com.ychat.common.Constants.Enums.Impl.RoleEnum;
import com.ychat.common.Constants.Enums.Impl.RoomTypeEnum;
import com.ychat.common.Constants.Exception.BusinessException;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.User.Dao.ContactDao;
import com.ychat.common.User.Dao.GroupMemberDao;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.*;
import com.ychat.common.User.Services.IRoleService;
import com.ychat.common.User.Services.IRoomService;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.User.Services.cache.UserInfoCache;
import com.ychat.common.Utils.Assert.AssertUtil;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@Slf4j
public class RoomAppServiceImpl implements RoomAppService {

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private HotRoomCache hotRoomCache;

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomGroupCache roomGroupCache;

    @Autowired
    private RoomFriendCache roomFriendCache;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private IRoomService roomService;

    @Autowired
    private UserCache userCache;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ChatService chatService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private PushService pushService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid) {
        // 查出用户要展示的会话列表
        CursorPageBaseResp<Long> page;

        if (Objects.nonNull(uid)) {
            Double hotEnd = getCursorOrNull(request.getCursor());
            Double hotStart = null;

            // 用户基础会话
            CursorPageBaseResp<Contact> contactPage = contactDao.getContactPage(uid, request);
            List<Long> baseRoomIds = contactPage.getList().stream().map(Contact::getRoomId).collect(Collectors.toList());
            if (!contactPage.getIsLast()) {
                hotStart = getCursorOrNull(contactPage.getCursor());
            }

            // 热门房间
            Set<ZSetOperations.TypedTuple<String>> typedTuples = hotRoomCache.getRoomRange(hotStart, hotEnd);
            List<Long> hotRoomIds = typedTuples.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(Long::parseLong).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);

            // 基础会话和热门房间合并
            page = CursorPageBaseResp.init(contactPage, baseRoomIds);
        } else { // 用户未登录，只查全局房间（全局房间一定是热门群聊）
            // Pair<Value, Score>
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(request);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }

        if (CollectionUtil.isEmpty(page.getList())) {
            return CursorPageBaseResp.empty();
        }

        // 最后组装会话信息（名称，头像，未读数等）
        List<ChatRoomResp> result = buildContactResp(uid, page.getList());
        return CursorPageBaseResp.init(page, result);
    }

    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor).map(Double::parseDouble).orElse(null);
    }

    @NotNull
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        // 表情和头像，构建会话展示信息第一步
        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(roomIds, uid);
        // 获取会话最后一条消息，构建会话展示信息第二步
        List<Long> msgIds = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        // 拿到所有会话最后一条消息集合
        List<Message> messages = CollectionUtil.isEmpty(msgIds) ? new ArrayList<>() : messageDao.listByIds(msgIds);
        // 消息 Id 到消息的映射
        Map<Long, Message> msgMap = messages.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        // 消息 Id 到该条消息发送者的映射关系
        Map<Long, User> lastMsgUidMap = userInfoCache.getBatch(messages.stream().map(Message::getFromUid).collect(Collectors.toList()));
        // 获取每条消息ID消息未读数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(uid, roomIds);
        return roomBaseInfoMap.values().stream().map(room -> {
                    ChatRoomResp resp = new ChatRoomResp();
                    // 取出已经完成第一步构建的会话基本信息
                    RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(room.getRoomId());
                    resp.setAvatar(roomBaseInfo.getAvatar());
                    resp.setRoomId(room.getRoomId());
                    resp.setActiveTime(room.getActiveTime());
                    resp.setHot_Flag(roomBaseInfo.getHotFlag());
                    resp.setType(roomBaseInfo.getType());
                    resp.setName(roomBaseInfo.getName());
                    // 取出最后一条消息内容
                    Message message = msgMap.get(room.getLastMsgId());
                    if (Objects.nonNull(message)) {
                        // 获取这条消息的类型处理器
                        AbstractMsgHandler<?> strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        // 加工消息展示格式
                        resp.setText(lastMsgUidMap.get(message.getFromUid()).getName() + ":" + strategyNoNull.showContactMsg(message));
                    }
                    // 设置本条会话还未读的消息数
                    resp.setUnreadCount(unReadCountMap.getOrDefault(room.getRoomId(), 0));
                    return resp;
                }).sorted(Comparator.comparing(ChatRoomResp::getActiveTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 根据会话 ids 封装会话详情信息
     * @param roomIds
     * @param uid
     * @return
     */
    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(List<Long> roomIds, Long uid) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        // 根据会话类型进行分组，单聊一个 List，群组一个 List，List 存的都是 RoomId
        Map<Integer, List<Long>> groupRoomIdMap = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));
        // 获取群聊会话信息
        List<Long> groupRoomId = groupRoomIdMap.get(RoomTypeEnum.GROUP.getType());
        Map<Long, RoomGroup> roomInfoBatch = roomGroupCache.getBatch(groupRoomId);
        // 获取单聊会话信息
        List<Long> friendRoomId = groupRoomIdMap.get(RoomTypeEnum.FRIEND.getType());
        // 通过单聊会话获取会话对象信息
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRoomId, uid);
        return roomMap.values().stream().map(room -> {
            RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
            // 填充基础会话 Room 信息
            roomBaseInfo.setRoomId(room.getId());
            roomBaseInfo.setType(room.getType());
            roomBaseInfo.setHotFlag(room.getHotFlag());
            roomBaseInfo.setLastMsgId(room.getLastMsgId());
            roomBaseInfo.setActiveTime(room.getActiveTime());
            // 如果这个会话是群聊会话
            if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                // 从群聊会话信息中取出
                RoomGroup roomGroup = roomInfoBatch.get(room.getId());
                roomBaseInfo.setName(roomGroup.getName());
                roomBaseInfo.setAvatar(roomGroup.getAvatar());
            } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND){ // 这个会话是单聊会话
                // 从单聊会话从取出
                User user = friendRoomMap.get(room.getId());
                roomBaseInfo.setName(user.getName());
                roomBaseInfo.setAvatar(user.getAvatar());
            }
            return roomBaseInfo;
        }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    /**
     * 根据单聊会话 RoomIDs 获取会话对象用户信息
     * @param roomIds
     * @param uid
     * @return
     */
    private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
        if (CollectionUtil.isEmpty(roomIds)) {
            return new HashMap<>();
        }
        // 查询单聊会话
        Map<Long, RoomFriend> roomFriendMap = roomFriendCache.getBatch(roomIds);
        // 获取单聊会话对应的对象 UID
        Set<Long> friendUidSet = ChatAdapter.getFriendUidSet(roomFriendMap.values(), uid);
        // 查询到被展示对象的 UserInfo
        Map<Long, User> userBatch = userInfoCache.getBatch(new ArrayList<>(friendUidSet));
        return roomFriendMap.values()
                .stream()
                .collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
                    Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
                    return userBatch.get(friendUid);
                }));
    }

    /**
     * 获取会话内的未读数
     */
    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
        List<Contact> contacts = contactDao.getByRoomIds(roomIds, uid);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    public ChatRoomResp getContactDetail(Long uid, Long roomId) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        // 构建单个会话
        return buildContactResp(uid, Collections.singletonList(roomId)).get(0);
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid) {
        RoomFriend friendRoom = roomService.getFriendRoom(uid, friendUid);
        AssertUtil.isNotEmpty(friendRoom, "他不是您的好友");
        return buildContactResp(uid, Collections.singletonList(friendRoom.getRoomId())).get(0);
    }

    /**
     * 获取群聊会话详情
     * @param uid
     * @param roomId
     * @return
     */
    @Override
    public MemberResp getGroupDetail(Long uid, long roomId) {
        RoomGroup roomGroup = roomGroupCache.get(roomId);
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(roomGroup, "roomId有误");
        Long onlineNum;
        if (room.isHotRoom()) { // 热点群（所有用户都会默认加入热点群聊） --> 从 Redis 获取人数
            onlineNum = userCache.getOnlineNum();
        } else {
            // 拿到所有群成员
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
            onlineNum = userDao.getOnlineCount(memberUidList).longValue();
        }
        // 获取自己在这个群聊会话的角色
        GroupRoleAPPEnum groupRole = getGroupRole(uid, roomGroup, room);
        return MemberResp.builder()
                .avatar(roomGroup.getAvatar())
                .roomId(roomId)
                .groupName(roomGroup.getName())
                .onlineNum(onlineNum)
                .role(groupRole.getType())
                .build();
    }

    /**
     * 获取用户在群聊会话的角色
     * @param uid
     * @param roomGroup
     * @param room
     * @return
     */
    private GroupRoleAPPEnum getGroupRole(Long uid, RoomGroup roomGroup, Room room) {
        GroupMember member = Objects.isNull(uid) ? null : groupMemberDao.getMember(roomGroup.getId(), uid);
        if (Objects.nonNull(member)) {
            return GroupRoleAPPEnum.of(member.getRole());
        } else if (room.isHotRoom()) {
            return GroupRoleAPPEnum.MEMBER;
        } else {
            return GroupRoleAPPEnum.REMOVE;
        }
    }

    /**
     * 组合游标获取 -- 群成员列表
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "会话不存在");
        // 群成员列表
        List<Long> memberUidList;
        if (room.isHotRoom()) { // 全员群展示所有用户（所有用户都需要参与排序）
            memberUidList = null;
        } else { // 普通会话，只展示房间内的群成员
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
        }
        return chatService.getMemberPage(memberUidList, request);
    }

    /**
     * 获取 @ 群成员列表，可以加缓存
     * @param request
     * @return
     */
    @Override
    @Cacheable(cacheNames = "member", key = "'memberList.'+#request.roomId")
    public List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "会话不存在");
        if (room.isHotRoom()) { // 全员群展示所有用户100名
            List<User> memberList = userDao.getMemberList();
            return MemberAdapter.buildMemberList(memberList);
        } else {
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId());
            Map<Long, User> batch = userInfoCache.getBatch(memberUidList);
            return MemberAdapter.buildMemberList(batch);
        }
    }

    /**
     * 移除群成员
     * @param uid
     * @param request
     */
    @Override
    public void delMember(Long uid, MemberDelReq request) {
        Room room = roomCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(room, "会话不存在");
        RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, "会话不存在");
        GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
        AssertUtil.isNotEmpty(self, GroupErrorEnum.USER_NOT_IN_GROUP);
        // 1. 判断被移除的人是否是群主或者管理员（群主不可以被移除，管理员只能被群主移除）
        Long removedUid = request.getUid();
        // 1.1 群主 非法操作
        AssertUtil.isFalse(groupMemberDao.isLord(roomGroup.getId(), removedUid), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        // 1.2 判断被移除人是否是管理员
        if (groupMemberDao.isManager(roomGroup.getId(), removedUid)) {
            // 判断操作人是否是群主
            Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
            AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        }
        // 1.3 普通成员 判断是否有权限操作
        AssertUtil.isTrue(hasRole(self), GroupErrorEnum.NOT_ALLOWED_FOR_REMOVE);
        GroupMember member = groupMemberDao.getMember(roomGroup.getId(), removedUid);
        AssertUtil.isNotEmpty(member, "用户已经移除");
        groupMemberDao.removeById(member.getId());
        // 发送移除事件告知群成员
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), member.getUid());
        pushService.sendPushMsg(ws, memberUidList);
        groupMemberCache.evictMemberUidList(room.getId());
    }

    private boolean hasRole(GroupMember self) {
        return Objects.equals(self.getRole(), GroupRoleEnum.LEADER.getType())
                || Objects.equals(self.getRole(), GroupRoleEnum.MANAGER.getType())
                || roleService.hasRole(self.getUid(), RoleEnum.ADMIN);
    }

    @Override
    public Long addGroup(Long uid, GroupAddReq request) {
        // 先创建一条群聊会话记录
        RoomGroup roomGroup = roomService.createGroupRoom(uid);
        // 批量保存群成员
        List<GroupMember> groupMembers = RoomAdapter.buildGroupMemberBatch(request.getUidList(), roomGroup.getId());
        groupMemberDao.saveBatch(groupMembers);
        // 发送邀请加群消息 ---> 触发每个人的会话
        applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
        return roomGroup.getRoomId();
    }

    /**
     * 邀请好友
     * @param uid
     * @param request
     */
    @Override
    public void addMember(Long uid, MemberAddReq request) {
        RLock lock = redissonClient.getLock("inventFriend:uid:" + uid);
        try {
            // 尝试加锁，设置锁的过期时间为5秒，防止长时间占用
            if (lock.tryLock(5, 2, TimeUnit.SECONDS)) {
                Room room = roomCache.get(request.getRoomId());
                AssertUtil.isNotEmpty(room, "会话不存在");
                AssertUtil.isFalse(room.isHotRoom(), "全员群无需邀请好友");
                RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
                AssertUtil.isNotEmpty(roomGroup, "会话不存在");
                GroupMember self = groupMemberDao.getMember(roomGroup.getId(), uid);
                AssertUtil.isNotEmpty(self, "您不在该群中，无法邀请其他人加入群聊");
                List<Long> memberBatch = groupMemberDao.getMemberBatch(roomGroup.getId(), request.getUidList());
                Set<Long> existUid = new HashSet<>(memberBatch);
                List<Long> waitAddUidList = request.getUidList().stream().filter(a -> !existUid.contains(a)).distinct().collect(Collectors.toList());
                if (CollectionUtils.isEmpty(waitAddUidList)) {
                    return;
                }
                List<GroupMember> groupMembers = MemberAdapter.buildMemberAdd(roomGroup.getId(), waitAddUidList);
                groupMemberDao.saveBatch(groupMembers);
                applicationEventPublisher.publishEvent(new GroupMemberAddEvent(this, roomGroup, groupMembers, uid));
            } else {
                throw new BusinessException("已经邀请过了，请勿频繁邀请");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁等待被中断");
        } finally {
            // 确保在操作结束后释放锁
            lock.unlock();
        }
    }




}
