package com.ychat.common.User.Event.Listener;

import com.ychat.common.User.Event.UserBlackEvent;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.Websocket.Services.WebSocketService;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class UserBlackListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 拉黑用户后，通知其他 Channel
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBlackMsg(UserBlackEvent event) {
        User user = event.getUser();
        log.info("侦听到了用户被拉黑事件：{}", user.toString());
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlack(user));
    }

    /**
     * 用户被拉黑后，更新用户的状态
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeUserStatus(UserBlackEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }

    /**
     * 用户被拉黑后，刷新被拉黑用户缓存
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void refreshCache(UserBlackEvent event) {
        userCache.evictBlackMap();
    }
}
