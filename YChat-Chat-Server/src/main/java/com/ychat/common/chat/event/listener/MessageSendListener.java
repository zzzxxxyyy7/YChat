package com.ychat.common.chat.event.listener;

import com.ychat.common.chat.Constant.MQConstant;
import com.ychat.common.chat.domain.dto.MsgSendMessageDTO;
import com.ychat.common.user.Event.MessageSendEvent;
import com.ychat.service.MQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class MessageSendListener {

    @Autowired
    private MQProducer mqProducer;

    /**
     * 监听到消息发送事件，在事务提交前执行，确保消息发送出去，被分布式事务框架捕获的时候，是处于事务中的
     * 保障消息的可靠性
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT, classes = MessageSendEvent.class, fallbackExecution = true)
    public void messageRoute(MessageSendEvent event) {
        Long msgId = event.getMsgId();
        mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }

}
