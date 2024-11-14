package com.ychat.common.user.Event.listener;

import com.ychat.common.Enums.UserActiveStatusEnum;
import com.ychat.common.user.Event.UserBlackEvent;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    private IpService ipService;

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBlackMsg(UserBlackEvent event) {
        User user = event.getUser();
        log.info("侦听到了用户被拉黑事件：{}", user.toString());
        User updateUser = new User();
        BeanUtils.copyProperties(user, updateUser);
        updateUser.setIpInfo(user.getIpInfo());
        updateUser.setLastOptTime(user.getLastOptTime());
        updateUser.setActiveStatus(UserActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(updateUser);
        ipService.refreshIpDetailAsync(user.getId());
    }
}
