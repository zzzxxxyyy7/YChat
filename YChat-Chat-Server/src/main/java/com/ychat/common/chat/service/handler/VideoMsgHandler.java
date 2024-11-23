package com.ychat.common.chat.service.handler;


import com.ychat.common.Constants.Enums.Impl.MessageTypeEnum;
import com.ychat.common.chat.domain.entity.msg.MessageExtra;
import com.ychat.common.chat.domain.entity.msg.VideoMsgDTO;
import com.ychat.common.user.dao.MessageDao;
import com.ychat.common.user.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 视频消息
 */
@Component
public class VideoMsgHandler extends AbstractMsgHandler<VideoMsgDTO> {

    @Autowired
    private MessageDao messageDao;

    @Override
    MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    public void saveMsg(Message msg, VideoMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(msg.getId());
        update.setExtra(extra);
        extra.setVideoMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        return msg.getExtra().getVideoMsgDTO();
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return "视频";
    }

    @Override
    public String showContactMsg(Message msg) {
        return "[视频]";
    }

}
