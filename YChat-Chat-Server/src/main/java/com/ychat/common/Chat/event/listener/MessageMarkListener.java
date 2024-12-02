package com.ychat.common.Chat.Event.listener;


import com.ychat.common.Chat.Event.MessageMarkEvent;
import com.ychat.common.Chat.domain.dto.ChatMessageMarkDTO;
import com.ychat.common.Constants.Enums.Impl.IdempotentEnum;
import com.ychat.common.Constants.Enums.Impl.ItemEnum;
import com.ychat.common.Constants.Enums.Impl.MessageMarkTypeEnum;
import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Dao.MessageMarkDao;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Services.IUserBackpackService;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.Websocket.Services.Adapter.WebSocketAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

/**
 * 消息标记监听器
 */
@Slf4j
@Component
public class MessageMarkListener {

    @Autowired
    private MessageMarkDao messageMarkDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private IUserBackpackService iUserBackpackService;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void changeMsgType(MessageMarkEvent event) {
        ChatMessageMarkDTO dto = event.getDto();
        // 查出消息
        Message msg = messageDao.getById(dto.getMsgId());
        // 暂时只有正常消息才能被标记、撤回和图片、文件、视频、语音均不可被标记
        if (!Objects.equals(msg.getType(), MessageTypeEnum.TEXT.getType())) {
            return;
        }
        // 消息被标记次数
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(dto.getMarkType());
        // 判断标记是否达到升级标准、执行发放徽章逻辑
        if (markCount < markTypeEnum.getRiseNum()) {
            return;
        }
        // 尝试给用户发送一张徽章
        // 如果是一条点赞类型，且符合升级标准
        if (MessageMarkTypeEnum.LIKE.getType().equals(dto.getMarkType())) {
            // 获取点赞徽章
            iUserBackpackService.acquireItem(msg.getFromUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MSG_ID, msg.getId().toString());
        }
    }

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notifyAll(MessageMarkEvent event) {
        // 后续可做合并查询，目前异步影响不大
        ChatMessageMarkDTO dto = event.getDto();
        // 查询这条消息被标记的次数，直接推送总次数，不采用 +1 +2 的方式，避免并发冲突
        Integer markCount = messageMarkDao.getMarkCount(dto.getMsgId(), dto.getMarkType());
        // MQ推送被标记后的消息给所有人
        pushService.sendPushMsg(WebSocketAdapter.buildMsgMarkSend(dto, markCount));
    }

}
