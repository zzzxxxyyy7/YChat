package com.ychat.common.user.service.adapter;

import Constants.Enums.MessageTypeEnum;
import com.ychat.common.user.dao.ChatMessageReq;
import com.ychat.common.user.domain.dto.TextMsgReq;

/**
 * Description: 消息适配器
 */
public class MessageAdapter {

    public static final int CAN_CALLBACK_GAP_COUNT = 100;

    public static ChatMessageReq buildAgreeMsg(Long roomId) {
        ChatMessageReq chatMessageReq = new ChatMessageReq();
        chatMessageReq.setRoomId(roomId);
        chatMessageReq.setMsgType(MessageTypeEnum.TEXT.getType());
        TextMsgReq textMsgReq = new TextMsgReq();
        textMsgReq.setContent("我们已经成为好友了，开始聊天吧");
        chatMessageReq.setBody(textMsgReq);
        return chatMessageReq;
    }
}
