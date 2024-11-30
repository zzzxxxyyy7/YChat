package com.ychat.common.Chat.Services.Impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.ychat.common.Chat.Services.handler.RecallMsgHandler;
import com.ychat.common.Chat.domain.dto.ChatMessageBaseReq;
import com.ychat.common.Chat.domain.dto.ChatMessagePageReq;
import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.Constants.Enums.Impl.NormalOrNoEnum;
import com.ychat.common.Constants.Enums.Impl.RoleEnum;
import com.ychat.common.User.Dao.*;
import com.ychat.common.User.Services.IRoleService;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.Utils.Assert.AssertUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ychat.common.Chat.domain.dto.ChatMessageReq;
import com.ychat.common.Chat.domain.vo.ChatMessageResp;
import com.ychat.common.Chat.Services.ChatService;
import com.ychat.common.Chat.Services.adapter.MessageAdapter;
import com.ychat.common.Chat.Services.cache.RoomCache;
import com.ychat.common.Chat.Services.cache.RoomGroupCache;
import com.ychat.common.Chat.Services.handler.AbstractMsgHandler;
import com.ychat.common.Chat.Services.factory.MsgHandlerFactory;
import com.ychat.common.Chat.Event.MessageSendEvent;
import com.ychat.common.User.Domain.entity.*;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberStatisticResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 消息处理类
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private RoomGroupCache roomGroupCache;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    private ContactDao contactDao;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private RecallMsgHandler recallMsgHandler;

    /**
     * 大群聊 ID 默认是 1
     */
    public static final long ROOM_GROUP_ID = 1L;

    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        // 校验发送会话的合法性
        check(request, uid);
        // 根据消息类型拿到对应的消息处理器
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
        Long msgId = msgHandler.checkAndSaveMsg(request, uid);
        // 发布消息发送事件
        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
        return msgId;
    }

    private void check(ChatMessageReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
        if (room.isHotRoom()) {//全员群跳过校验
            return;
        }
        if (room.isRoomFriend()) {
            RoomFriend roomFriend = roomFriendDao.getByRoomId(request.getRoomId());
            AssertUtil.equal(NormalOrNoEnum.NORMAL.getStatus(), roomFriend.getStatus(), "您已经被对方拉黑");
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "您已经被对方拉黑");
        }
        if (room.isRoomGroup()) {
            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
            GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(member, "您已经被移除该群");
        }

    }

    @Override
    public ChatMessageResp getMsgResp(Long msgId, Long receiveUid) {
        Message msg = messageDao.getById(msgId);
        return getMsgResp(msg, receiveUid);
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
    }

    public List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollectionUtil.isEmpty(messages)) {
            return new ArrayList<>();
        }
        // 查询消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, msgMark, receiveUid);
    }

    @Override
    public ChatMemberStatisticResp getMemberStatistic() {
        // 拿到在线人数
        Long onlineNum = userCache.getOnlineNum();
        ChatMemberStatisticResp resp = new ChatMemberStatisticResp();
        resp.setOnlineNum(onlineNum);
        return resp;
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid) {
        // 用最后一条消息id，来限制被踢出的人能看见的最大一条消息
        Long lastMsgId = getLastMsgId(request.getRoomId(), receiveUid);
        CursorPageBaseResp<Message> cursorPage = messageDao.getCursorPage(request.getRoomId(), request, lastMsgId);
        if (cursorPage.isEmpty()) {
            return CursorPageBaseResp.empty();
        }
        return CursorPageBaseResp.init(cursorPage, getMsgRespBatch(cursorPage.getList(), receiveUid));
    }

    /**
     * 获取一个会话最后一条消息 ID
     * @param roomId
     * @param receiveUid
     * @return
     */
    private Long getLastMsgId(Long roomId, Long receiveUid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "会话号有误");
        if (room.isHotRoom()) {
            return null;
        }
        AssertUtil.isNotEmpty(receiveUid, "请先登录");
        Contact contact = contactDao.get(receiveUid, roomId);
        return contact.getLastMsgId();
    }

    /**
     * 撤回一条消息
     * @param uid
     * @param request
     */
    @Override
    public void recallMsg(Long uid, ChatMessageBaseReq request) {
        // 查询出消息
        Message message = messageDao.getById(request.getMsgId());
        // 校验是否拥有撤回的权力
        checkRecall(uid, message);
        // 执行撤回
        recallMsgHandler.recall(uid, message);
    }

    /**
     * 消息这条消息是否可以被撤回
     * @param uid 发起撤回申请人的 UID
     * @param message 需要被撤回的消息
     */
    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息有误");
        // 校验消息是否已经被撤回
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "已经撤回的消息无法再次撤回");
        // 是否拥有管理员权限
        boolean hasPower = roleService.hasRole(uid, RoleEnum.CHAT_MANAGER);
        if (hasPower) return;
        // 如果没有管理员权限，校验撤回的消息是否是自己发出的，不是自己的不能撤回
        AssertUtil.equal(uid, message.getFromUid(), "抱歉,您没有权限");
        long intervalTime = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(intervalTime < 2, "覆水难收，已经超过2分钟的消息不能撤回");
    }



}
