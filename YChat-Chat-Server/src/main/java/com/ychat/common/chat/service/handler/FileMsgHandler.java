package com.ychat.common.chat.service.handler;

import Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.chat.domain.entity.msg.FileMsgDTO;
import com.ychat.common.chat.domain.entity.msg.MessageExtra;
import com.ychat.common.user.dao.MessageDao;
import com.ychat.common.user.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 文件消息
 */
@Component
public class FileMsgHandler extends AbstractMsgHandler<FileMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.FILE;
    }

    @Override
    public void saveMsg(Message msg, FileMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setFileMsg(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getFileMsg();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "文件:" + msg.getExtra().getFileMsg().getFileName();
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[文件]" + msg.getExtra().getFileMsg().getFileName();
    }

}
