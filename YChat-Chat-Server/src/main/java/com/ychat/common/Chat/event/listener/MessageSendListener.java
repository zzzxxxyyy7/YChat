package com.ychat.common.Chat.Event.listener;

import com.ychat.common.Chat.Constant.MQConstant;
import com.ychat.common.Chat.Services.cache.RoomCache;
import com.ychat.common.Chat.domain.dto.MsgSendMessageDTO;
import com.ychat.common.Chat.Event.MessageSendEvent;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Domain.entity.Room;
import com.ychat.service.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import javax.validation.constraints.NotNull;

@Component
@Slf4j
public class MessageSendListener {

    @Autowired
    private MQProducer mqProducer;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RoomCache roomCache;

    /**
     * 监听到消息发送事件，在事务提交前执行，确保消息发送出去，被分布式事务框架捕获的时候，是处于事务中的
     * 保障消息的可靠性
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        // MsgID 作为 Message 的 Key
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }

    /**
     * 如果我 @ 的是一个 ChatGPT 机器人
     * @param event
     */
    @TransactionalEventListener(classes = MessageSendEvent.class, fallbackExecution = true)
    public void handlerMsg(@NotNull MessageSendEvent event) {
        Message message = messageDao.getById(event.getMsgId());
        Room room = roomCache.get(message.getRoomId());
        if (room.isHotRoom()) {
            //openAIService.chat(message);
        }
    }

    // TODO 补齐被 @ 用户的微信推送

}
