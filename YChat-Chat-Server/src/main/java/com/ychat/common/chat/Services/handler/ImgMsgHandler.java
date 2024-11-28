package com.ychat.common.Chat.Services.handler;

import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.Chat.domain.entity.msg.ImgMsgDTO;
import com.ychat.common.Chat.domain.entity.msg.MessageExtra;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 图片消息
 */
@Component
public class ImgMsgHandler extends AbstractMsgHandler<ImgMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMG;
    }

    @Override
    public void saveMsg(Message msg, ImgMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setImgMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getImgMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "图片";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[图片]";
    }

}
