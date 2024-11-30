package com.ychat.common.Chat.Event.listener;

import com.ychat.common.Chat.Event.MessageRecallEvent;
import com.ychat.common.Chat.Services.cache.MsgCache;
import com.ychat.common.Chat.domain.dto.ChatMsgRecallDTO;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class MessageRecallListener {

    @Autowired
    private MsgCache msgCache;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void evictMsg(MessageRecallEvent event) {
        ChatMsgRecallDTO recallDTO = event.getRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }

    @Async
    @TransactionalEventListener(classes = MessageRecallEvent.class, fallbackExecution = true)
    public void sendToAll(MessageRecallEvent event) {
        pushService.sendPushMsg(WebSocketAdapter.buildMsgRecall(event.getRecallDTO()));
    }

}
