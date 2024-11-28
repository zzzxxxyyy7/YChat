package com.ychat.common.Chat.Services.handler;


import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.Chat.domain.entity.msg.EmojisMsgDTO;
import com.ychat.common.Chat.domain.entity.msg.MessageExtra;
import com.ychat.common.User.Dao.MessageDao;
import com.ychat.common.User.Domain.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Emojis 表情消息
 */
@Component
@Slf4j
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Override
    public MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.EMOJI;
    }

    @Override
    public void saveMsg(Message msg, EmojisMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setEmojisMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getEmojisMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "表情";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[表情包]";
    }

}
