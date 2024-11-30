package com.ychat.common.Chat.Services;

import com.ychat.common.Chat.domain.dto.ChatMessageBaseReq;
import com.ychat.common.Chat.domain.dto.ChatMessagePageReq;
import com.ychat.common.Chat.domain.dto.ChatMessageReq;
import com.ychat.common.Chat.domain.vo.ChatMessageResp;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberStatisticResp;

import javax.annotation.Nullable;
import javax.validation.Valid;

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

    /**
     * 获取消息记录列表
     *
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, @Nullable Long receiveUid);

    /**
     * 撤回消息
     * @param uid
     * @param request
     */
    void recallMsg(Long uid, @Valid ChatMessageBaseReq request);
}
