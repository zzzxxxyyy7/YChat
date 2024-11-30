package com.ychat.common.Chat.Services.handler;


import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.Chat.domain.dto.ChatMsgRecallDTO;
import com.ychat.common.Chat.domain.entity.msg.MessageExtra;
import com.ychat.common.Chat.domain.entity.msg.MsgRecall;
import com.ychat.common.Chat.Event.MessageRecallEvent;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Description: 撤回文本消息
 */
@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object> {

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private UserCache userCache;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public void saveMsg(Message msg, Object body) {
        throw new UnsupportedOperationException();
    }

    /**
     * 规定被撤回的消息的展示格式
     * @param msg
     * @return
     */
    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        // 判断消息撤回人和消息发送人是否是同一个人，如果不同，则是管理员撤回，否则是个人撤回
        if (!Objects.equals(recall.getRecallUid(), msg.getFromUid())) {
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return "\"" + userInfo.getName() + "\"撤回了一条消息";
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "原消息已被撤回";
    }

    public void recall(Long recallUid, Message message) { //todo 消息覆盖问题用版本号解决
        MessageExtra extra = message.getExtra();
        // 更新消息扩展的撤回属性
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message newMessage = new Message();
        newMessage.setId(message.getId());
        newMessage.setType(MessageTypeEnum.RECALL.getType());
        newMessage.setExtra(extra);
        messageDao.updateById(newMessage);
        // 发布一个消息被撤回的事件
        applicationEventPublisher.publishEvent(new MessageRecallEvent(this, new ChatMsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));
    }

    @Override
    public String showContactMsg(Message msg) {
        return "撤回了一条消息";
    }

}
