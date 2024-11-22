package com.ychat.common.chat.service;

import com.ychat.common.chat.domain.dto.ChatMessageReq;

/**
 * Description: 消息处理类
 */
public interface ChatService {

    /**
     * 发送消息
     *
     * @param request
     */
    Long sendMsg(ChatMessageReq request, Long uid);

}
