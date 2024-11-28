package com.ychat.common.user.Consumer;

import com.ychat.common.chat.Constant.MQConstant;
import com.ychat.common.chat.Enum.WSPushTypeEnum;
import com.ychat.common.chat.domain.dto.PushMessageDTO;
import com.ychat.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户消费：接收消息
 */
@RocketMQMessageListener(topic = MQConstant.PUSH_TOPIC, consumerGroup = MQConstant.PUSH_GROUP, messageModel = MessageModel.BROADCASTING)
@Component
@Slf4j
public class PushConsumer implements RocketMQListener<PushMessageDTO> {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void onMessage(PushMessageDTO message) {
        // 拿到消息目标用户的类型
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(message.getPushType());

        switch (wsPushTypeEnum) {
            case USER: // 推送个人
                message.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(message.getWsBaseMsg(), uid);
                });
                break;
            case ALL: // 推送全员
                webSocketService.sendToAllOnline(message.getWsBaseMsg(), null);
                break;
        }
    }

}
