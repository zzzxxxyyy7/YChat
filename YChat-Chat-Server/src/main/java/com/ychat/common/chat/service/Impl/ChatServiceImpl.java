package com.ychat.common.chat.service.Impl;

import Constants.Enums.NormalOrNoEnum;
import Utils.Assert.AssertUtil;
import com.ychat.common.chat.domain.dto.ChatMessageReq;
import com.ychat.common.chat.service.ChatService;
import com.ychat.common.chat.service.cache.RoomCache;
import com.ychat.common.user.Event.MessageSendEvent;
import com.ychat.common.user.dao.RoomFriendDao;
import com.ychat.common.user.domain.entity.Room;
import com.ychat.common.user.domain.entity.RoomFriend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 消息处理类
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private RoomFriendDao roomFriendDao;


    /**
     * 大群聊 ID 默认是 1
     */
    public static final long ROOM_GROUP_ID = 1L;

    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        check(request, uid);
//        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
//        Long msgId = msgHandler.checkAndSaveMsg(request, uid);
        //发布消息发送事件
//        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
//        return msgId;
        return null;
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
//        if (room.isRoomGroup()) {
//            RoomGroup roomGroup = roomGroupCache.get(request.getRoomId());
//            GroupMember member = groupMemberDao.getMember(roomGroup.getId(), uid);
//            AssertUtil.isNotEmpty(member, "您已经被移除该群");
//        }

    }

}
