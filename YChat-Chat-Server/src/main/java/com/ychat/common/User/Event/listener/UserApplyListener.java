package com.ychat.common.User.Event.Listener;

import com.ychat.common.User.Event.UserApplyEvent;
import com.ychat.common.User.Dao.UserApplyDao;
import com.ychat.common.User.Domain.entity.UserApply;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSFriendApply;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
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
