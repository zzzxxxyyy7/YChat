package com.ychat.common.User.Event.Listener;

import com.ychat.common.Constants.Enums.Impl.UserActiveStatusEnum;
import com.ychat.common.User.Event.UserOnlineEvent;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.User.Services.IpService;
import com.ychat.common.User.Services.cache.UserCache;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class UserOnlineListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IpService ipService;

    @Autowired
    private UserCache userCache;

    @Autowired
    private PushService pushService;

    @Autowired
    private WebSocketAdapter webSocketAdapter;

    /**
     * 更新用户Ip、在线状态、最后上线时间等等
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void saveIp(UserOnlineEvent event) {
        User user = event.getUser();
        log.info("侦听到了用户上线事件：{}", user.toString());
        User updateUser = new User();
        BeanUtils.copyProperties(user, updateUser);
        updateUser.setIpInfo(user.getIpInfo());
        updateUser.setLastOptTime(user.getLastOptTime());
        updateUser.setActiveStatus(UserActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(updateUser);
        ipService.refreshIpDetailAsync(user.getId());
    }

    /**
     * 更新 Redis 中的在线表和离线表
     * @param event
     */
    @Async
    @EventListener(classes = UserOnlineEvent.class)
    public void saveRedisAndPush(UserOnlineEvent event) {
        User user = event.getUser();
        userCache.online(user.getId(), user.getLastOptTime());
        // 推送给所有在线用户，该用户登录成功
        pushService.sendPushMsg(webSocketAdapter.buildOnlineNotifyResp(event.getUser()));
    }

}
