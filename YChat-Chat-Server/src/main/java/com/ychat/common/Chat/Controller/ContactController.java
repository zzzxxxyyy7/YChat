package com.ychat.common.Chat.Controller;



import com.ychat.common.Chat.Services.ChatService;
import com.ychat.common.Chat.Services.RoomAppService;
import com.ychat.common.Chat.domain.dto.ContactFriendReq;
import com.ychat.common.Chat.domain.vo.ChatRoomResp;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.User.Domain.dto.req.IdReqVO;
import com.ychat.common.Utils.Request.CursorPageBaseResp;
import com.ychat.common.Utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 会话相关接口
 */
@RestController
@RequestMapping("/capi/chat")
@Api(tags = "会话列表接口")
@Slf4j
public class ContactController {

    @Autowired
    private RoomAppService roomService;

    @GetMapping("/public/contact/page")
    @ApiOperation("会话列表")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactPage(request, uid));
    }

    @GetMapping("/public/contact/detail")
    @ApiOperation("新消息推送时创建新的会话详情")
    public ApiResult<ChatRoomResp> getContactDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactDetail(uid, request.getId()));
    }

    @GetMapping("/public/contact/detail/friend")
    @ApiOperation("从好友列表发消息时创建会话详情")
    public ApiResult<ChatRoomResp> getContactDetailByFriend(@Valid ContactFriendReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactDetailByFriend(uid, request.getUid()));
    }

}
