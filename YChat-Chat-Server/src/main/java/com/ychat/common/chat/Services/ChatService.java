package com.ychat.common.Chat.Services;

import com.ychat.common.Chat.domain.dto.*;
import com.ychat.common.Chat.domain.vo.ChatMessageReadResp;
import com.ychat.common.Chat.domain.vo.ChatMessageResp;
import com.ychat.common.Chat.domain.vo.MsgReadInfoDTO;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.ChatMemberStatisticResp;

import javax.annotation.Nullable;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

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

    /**
     * 标记一条消息
     * @param uid
     * @param request
     */
    void setMsgMark(Long uid, @Valid ChatMessageMarkReq request);

    /**
     * 上报用户自身在某个会话下的最新阅读时间
     * @param uid
     * @param request
     */
    void msgRead(Long uid, ChatMessageMemberReq request);

    /**
     * 查询消息的已读和未读总数
     * @param uid
     * @param request
     * @return
     */
    Collection<MsgReadInfoDTO> getMsgReadInfo(Long uid, @Valid ChatMessageReadInfoReq request);

    /**
     *
     * @param uid
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMessageReadResp> getReadPage(Long uid, @Valid ChatMessageReadReq request);

    /**
     * 获取群成员列表
     *
     * @param memberUidList
     * @param request
     * @return
     */
    CursorPageBaseResp<ChatMemberResp> getMemberPage(List<Long> memberUidList, MemberReq request);

}
