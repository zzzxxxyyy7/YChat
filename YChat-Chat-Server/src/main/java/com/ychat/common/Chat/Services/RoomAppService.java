package com.ychat.common.Chat.Services;

import com.ychat.common.Chat.domain.vo.ChatRoomResp;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.Utils.Request.CursorPageBaseResp;

import javax.validation.Valid;

/**
 * 会话列表处理接口
 */
public interface RoomAppService {

    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(@Valid CursorPageBaseReq request, Long uid);





}
