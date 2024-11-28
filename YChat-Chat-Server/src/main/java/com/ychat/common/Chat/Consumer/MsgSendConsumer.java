package com.ychat.common.Chat.Consumer;

import com.ychat.common.Constants.Enums.Impl.RoomTypeEnum;
import com.ychat.common.Chat.Constant.MQConstant;
import com.ychat.common.Chat.domain.dto.MsgSendMessageDTO;
import com.ychat.common.Chat.domain.vo.ChatMessageResp;
import com.ychat.common.Chat.Services.ChatService;
import com.ychat.common.Chat.Services.cache.GroupMemberCache;
import com.ychat.common.Chat.Services.cache.HotRoomCache;
import com.ychat.common.Chat.Services.cache.RoomCache;
import com.ychat.common.User.Dao.ContactDao;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Dao.RoomDao;
import com.ychat.common.User.Dao.RoomFriendDao;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Domain.entity.Room;
import com.ychat.common.User.Domain.entity.RoomFriend;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Description: 发送消息更新房间收信箱，并同步给房间成员信箱
 */
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RoomCache roomCache;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private HotRoomCache hotRoomCache;

    @Autowired
    private PushService pushService;

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private ContactDao contactDao;

    /**
     * 当从 Topic 中拉到消息后，执行此方法
     * @param dto
     */
    @Override
    public void onMessage(MsgSendMessageDTO dto) {
        // 拿到发送的消息
        Message message = messageDao.getById(dto.getMsgId());
        // 拿到消息发往的会话
        Room room = roomCache.get(message.getRoomId());
        // 根据消息获取需要展示回前端的信息
        ChatMessageResp msgResp = chatService.getMsgResp(message, null);

        // 所有房间更新房间最新消息
        roomDao.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
        roomCache.delete(room.getId());

        if (room.isHotRoom()) { // 热门群聊推送所有在线的人
            // 更新热门群聊时间 - redis
            hotRoomCache.refreshActiveTime(room.getId(), message.getCreateTime());
            // 推送所有人
            pushService.sendPushMsg(WebSocketAdapter.buildMsgSend(msgResp));
        } else {
            // 存储需要将消息展示到的群成员ID列表
            List<Long> memberUidList = new ArrayList<>();

            if (Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())) { // 普通群聊会话 --> 推送所有群成员
                memberUidList = groupMemberCache.getMemberUidList(room.getId());
            } else if (Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())) { // 单聊会话 ---> 推送给单聊对象
                // 对单人推送
                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memberUidList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }

            // 更新所有群成员的会话时间
            contactDao.refreshOrCreateActiveTime(room.getId(), memberUidList, message.getId(), message.getCreateTime());

            // 推送房间成员
            pushService.sendPushMsg(WebSocketAdapter.buildMsgSend(msgResp), memberUidList);
        }
    }

}
