package com.ychat.common.User.Event.Listener;

import com.ychat.common.Constants.Enums.Impl.ChatActiveStatusEnum;
import com.ychat.common.User.Event.UserOfflineEvent;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.Websocket.Services.WebSocketService;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserOfflineListener {

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    private WebSocketAdapter webSocketAdapter;

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent event) {
        User user = event.getUser();
        userCache.offline(user.getId(), user.getLastOptTime());
        // 推送给所有在线用户，该用户下线
        webSocketService.sendToAllOnline(webSocketAdapter.buildOfflineNotifyResp(event.getUser()), event.getUser().getId());
    }

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveUserOfflineStatus(UserOfflineEvent event) {
        User user = event.getUser();
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setLastOptTime(user.getLastOptTime());
        newUser.setActiveStatus(ChatActiveStatusEnum.OFFLINE.getStatus());
        userDao.updateById(newUser);
    }

}
