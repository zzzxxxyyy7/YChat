package com.ychat.common.user.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import Constants.front.Request.CursorPageBaseReq;
import Constants.front.Request.PageBaseReq;
import Constants.front.Response.PageBaseResp;
import com.ychat.common.user.Event.UserApplyEvent;
import com.ychat.common.user.dao.UserApplyDao;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.dao.UserFriendDao;
import com.ychat.common.user.domain.dto.req.FriendApplyReq;
import com.ychat.common.user.domain.dto.req.FriendApproveReq;
import com.ychat.common.user.domain.dto.req.FriendCheckReq;
import com.ychat.common.user.domain.entity.RoomFriend;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.entity.UserApply;
import com.ychat.common.user.domain.entity.UserFriend;
import com.ychat.common.user.domain.vo.FriendApplyResp;
import com.ychat.common.user.domain.vo.FriendCheckResp;
import com.ychat.common.user.domain.vo.FriendResp;
import com.ychat.common.user.domain.vo.FriendUnreadResp;
import com.ychat.common.user.service.IRoomFriendService;
import com.ychat.common.user.service.IUserFriendService;
import com.ychat.common.user.service.adapter.FriendAdapter;
import Utils.Assert.AssertUtil;
import com.ychat.common.utils.Request.CursorPageBaseResp;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static Constants.Enums.Impl.ApplyStatusEnum.WAIT_APPROVAL;

@Service
@Slf4j
public class UserFriendServiceImpl implements IUserFriendService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserFriendDao userFriendDao;

    @Autowired
    private UserApplyDao userApplyDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private IRoomFriendService roomService;

//    @Autowired
//    private ChatService chatService;


    /**
     * 检查是否是自己好友
     *
     * @param uid     uid
     * @param request 请求
     * @return {@link FriendCheckResp}
     */
    @Override
    public FriendCheckResp checkIsMyFriends(Long uid, FriendCheckReq request) {
        List<UserFriend> friendList = userFriendDao.checkIsMyFriends(uid, request.getUidList());
        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());

        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());

        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 发送好友申请
     *
     * @param request 请求
     */
    @Override
    public void sendFriendApply(Long uid, FriendApplyReq request) {

        // 使用 Redisson 获取分布式锁
        RLock lock = redissonClient.getLock("applyFriend:uid:" + uid);

        try {
            // 尝试加锁，设置锁的过期时间为5秒，防止长时间占用
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                // 是否有好友关系
                UserFriend friend = userFriendDao.checkIsMyFriend(uid, request.getTargetUid());
                AssertUtil.isEmpty(friend, "你们已经是好友了");

                // 是否已经存在好友申请记录但是暂未同意（避免重复申请）
                UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid());
                if (Objects.nonNull(selfApproving)) {
                    log.info("已经申请过了,uid:{}, targetId:{}", uid, request.getTargetUid());
                    return;
                }

                // 对方是否申请过我的好友，如果有，直接同意申请
                UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid);
                if (Objects.nonNull(friendApproving)) {
                    ((IUserFriendService) AopContext.currentProxy()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
                    return;
                }

                // 初始化一条申请记录 -- 待审批、未读
                UserApply newUserApply = FriendAdapter.buildFriendApply(uid, request);
                userApplyDao.save(newUserApply);

                //申请事件
                applicationEventPublisher.publishEvent(new UserApplyEvent(this, newUserApply));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁等待被中断");
        } finally {
            // 确保在操作结束后释放锁
            if (lock != null) {
                lock.unlock();
            }
        }

    }

    /**
     * 获取好友申请记录列表 - 普通翻页
     * @param uid
     * @param request 请求
     * @return
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid, request.plusPage());
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())) {
            return PageBaseResp.empty();
        }
        // 将这些申请列表设为已读
        readApples(uid, userApplyIPage);
        //返回消息
        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    private void readApples(Long uid, IPage<UserApply> userApplyIPage) {
        List<Long> applyIds = userApplyIPage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        userApplyDao.readApples(uid, applyIds);
    }

    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    /**
     * 同意好友申请
     * @param uid     uid
     * @param request 请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyApprove(Long uid, FriendApproveReq request) {
        RLock lock = redissonClient.getLock("applyApprove:uid:" + uid);
        try {
            // 尝试加锁，设置锁的过期时间为5秒，防止长时间占用
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                 UserApply userApply = userApplyDao.getById(request.getApplyId());
                AssertUtil.isNotEmpty(userApply, "不存在申请记录");
                AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录");
                AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "已同意好友申请");

                // 同意申请
                userApplyDao.agree(request.getApplyId());

                // 创建双方好友关系
                createUserFriend(uid, userApply.getUid());

                //创建一个聊天房间
                RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, userApply.getUid()));

                //发送一条同意消息，我们已经是好友了，开始聊天吧
                //chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId()), uid);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁等待被中断");
        } finally {
            // 确保在操作结束后释放锁
            if (lock != null) {
                lock.unlock();
            }
        }

    }

    /**
     * 创建一条好友记录
     * @param uid
     * @param targetUid
     */
    private void createUserFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend1, userFriend2));
    }

    /**
     * 删除好友
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    @Override
    public void deleteFriend(Long uid, Long friendUid) {
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, friendUid);
        if (CollectionUtil.isEmpty(userFriends)) {
            log.info("{} 和 {} 没有好友关系", uid, friendUid);
            return;
        }

        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);

        //禁用房间
        //roomService.disableFriendRoom(Arrays.asList(uid, friendUid));
    }

    /**
     * 获取用户好友列表 - 游标翻页 - 解决深翻页问题
     * @param uid
     * @param request
     * @return
     */
    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        if (CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }

        List<Long> friendUids = friendPage.getList()
                .stream().map(UserFriend::getFriendUid)
                .collect(Collectors.toList());

        // 批量查询好友信息
        List<User> userList = userDao.getFriendList(friendUids);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), userList));
    }

}
