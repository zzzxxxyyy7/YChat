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
 * 默认集群：CLUSTERING，必须是广播模式：BROADCASTING
 * 在集群模式下，同一个消费者组（Consumer Group）中的消费者实例平均分摊消费消息。即，一个消息只会被消费者组中的一个消费者消费一次，从而实现负载均衡
 * 在广播模式下，同一个消息会被分发给消费者组中的每一个消费者实例
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
