package com.ychat.common.chat.Consumer;

import com.ychat.common.chat.Constant.MQConstant;
import com.ychat.common.chat.domain.dto.MsgSendMessageDTO;
import com.ychat.common.chat.domain.vo.ChatMessageResp;
import com.ychat.common.chat.service.ChatService;
import com.ychat.common.chat.service.cache.RoomCache;
import com.ychat.common.user.dao.MessageDao;
import com.ychat.common.user.dao.RoomDao;
import com.ychat.common.user.domain.entity.Message;
import com.ychat.common.user.domain.entity.Room;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    }

}
