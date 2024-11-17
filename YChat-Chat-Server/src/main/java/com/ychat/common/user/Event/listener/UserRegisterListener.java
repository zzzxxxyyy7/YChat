package com.ychat.common.user.Event.listener;

import Constants.Enums.IdempotentEnum;
import Constants.Enums.ItemEnum;
import com.ychat.common.user.Event.UserRegisterEvent;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IUserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
public class UserRegisterListener {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IUserBackpackService iUserBackpackService;

    /**
     * 发放改名卡，因为用户注册是必须的，改名卡发放可以失败，所以改为异步
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(UserRegisterEvent event) {
        User user = event.getUser();
        log.info("侦听到了用户注册发放改名卡：{}", user.toString());
        //送一张改名卡
        iUserBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    /**
     * 发放徽章
     * @param event
     */
    @Async
    @TransactionalEventListener(classes = UserRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(UserRegisterEvent event) {
        User user = event.getUser();
        int registerCount = userDao.count();
        if (registerCount < 10) {
            // 前十名徽章
            log.info("侦听到了用户注册发放前十名徽章事件：{}", user.toString());
            iUserBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (registerCount < 100) {
            // 前一百名徽章
            log.info("侦听到了用户注册发放前一百名徽章事件：{}", user.toString());
            iUserBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }
}
