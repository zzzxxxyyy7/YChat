package com.ychat.common.chat.service;

import com.ychat.common.chat.domain.dto.ChatMessageReq;
import com.ychat.common.chat.domain.vo.ChatMessageResp;
import com.ychat.common.user.domain.entity.Message;
import com.ychat.common.websocket.domain.vo.resp.ChatMemberStatisticResp;

/**
 * 消息处理类
 */
public interface ChatService {

    /**
     * 发送消息
     *
     * @param request
     */
    Long sendMsg(ChatMessageReq request, Long uid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param msgId
     * @param receiveUid 接受消息的uid，可null
     * @return
     */
    ChatMessageResp getMsgResp(Long msgId, Long receiveUid);

    /**
     * 根据消息获取消息前端展示的物料
     *
     * @param message
     * @param receiveUid 接受消息的uid，可null
     * @return
     */
    ChatMessageResp getMsgResp(Message message, Long receiveUid);

    /**
     * 获取在线人数
     * @return
     */
    ChatMemberStatisticResp getMemberStatistic();

}
