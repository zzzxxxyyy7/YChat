package com.ychat.common.Chat.Services.Impl;

import com.ychat.common.Constants.Enums.Impl.NormalOrNoEnum;
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
import com.ychat.common.User.Event.MessageSendEvent;
import com.ychat.common.User.Dao.GroupMemberDao;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Dao.MessageMarkDao;
import com.ychat.common.User.Dao.RoomFriendDao;
import com.ychat.common.User.Domain.entity.*;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberStatisticResp;
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

    @Autowired
    private UserCache userCache;

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

}
