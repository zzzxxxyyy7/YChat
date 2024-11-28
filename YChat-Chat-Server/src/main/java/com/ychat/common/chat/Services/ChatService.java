package com.ychat.common.Chat.Services;

import com.ychat.common.Chat.domain.dto.ChatMessageReq;
import com.ychat.common.Chat.domain.vo.ChatMessageResp;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberStatisticResp;

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
