package com.ychat.common.user.Event.listener;

import com.ychat.common.user.Event.UserApplyEvent;
import com.ychat.common.user.dao.UserApplyDao;
import com.ychat.common.user.domain.entity.UserApply;
import com.ychat.common.user.service.Impl.PushService;
import com.ychat.common.websocket.domain.vo.resp.WSFriendApply;
import com.ychat.common.websocket.service.adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 好友申请监听器
 */
@Component
@Slf4j
public class UserApplyListener {

    @Autowired
    private UserApplyDao userApplyDao;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, fallbackExecution = true)
    public void notifyFriend(UserApplyEvent event) {
        UserApply userApply = event.getUserApply();
        // 获取申请目标当前的好友申请未读数量
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        // 推送申请目标（即TargetId）的客户端
        pushService.sendPushMsg(WebSocketAdapter.buildApplySend(new WSFriendApply(userApply.getUid(), unReadCount)));
    }

}
