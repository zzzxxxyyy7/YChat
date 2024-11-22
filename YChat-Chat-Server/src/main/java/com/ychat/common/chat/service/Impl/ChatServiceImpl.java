package com.ychat.common.chat.service.Impl;

import com.ychat.common.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Description: 消息处理类
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public static final long ROOM_GROUP_ID = 1L;

//    /**
//     * 发送消息
//     */
//    @Override
//    @Transactional
//    public Long sendMsg(ChatMessageReq request, Long uid) {
//        //check(request, uid);
//        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(request.getMsgType());
//        Long msgId = msgHandler.checkAndSaveMsg(request, uid);
//        //发布消息发送事件
//        applicationEventPublisher.publishEvent(new MessageSendEvent(this, msgId));
//        return msgId;
//    }

}
