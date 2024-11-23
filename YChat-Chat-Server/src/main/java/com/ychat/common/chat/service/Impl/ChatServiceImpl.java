package com.ychat.common.chat.service.Impl;

import Constants.Enums.Impl.NormalOrNoEnum;
import Utils.Assert.AssertUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.ychat.common.chat.domain.dto.ChatMessageReq;
import com.ychat.common.chat.domain.vo.ChatMessageResp;
import com.ychat.common.chat.service.ChatService;
import com.ychat.common.chat.service.adapter.MessageAdapter;
import com.ychat.common.chat.service.cache.RoomCache;
import com.ychat.common.chat.service.cache.RoomGroupCache;
import com.ychat.common.chat.service.handler.AbstractMsgHandler;
import com.ychat.common.chat.service.factory.MsgHandlerFactory;
import com.ychat.common.user.Event.MessageSendEvent;
import com.ychat.common.user.dao.GroupMemberDao;
import com.ychat.common.user.dao.MessageDao;
import com.ychat.common.user.dao.MessageMarkDao;
import com.ychat.common.user.dao.RoomFriendDao;
import com.ychat.common.user.domain.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        //发布消息发送事件
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
        //查询消息标志
        List<MessageMark> msgMark = messageMarkDao.getValidMarkByMsgIdBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));
        return MessageAdapter.buildMsgResp(messages, msgMark, receiveUid);
    }

}
