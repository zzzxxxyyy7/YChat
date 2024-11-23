package com.ychat.common.chat.Controller;

import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.chat.domain.vo.ChatMessageResp;
import com.ychat.common.chat.domain.dto.ChatMessageReq;
import com.ychat.common.chat.service.ChatService;
import com.ychat.common.utils.Request.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/chat")
@Api(tags = "聊天室相关接口")
@Slf4j
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/private/msg")
    @ApiOperation("发送消息")
    // 频控、暂时注释
//    @FrequencyControl(time = 5, count = 3, target = FrequencyControl.Target.UID)
//    @FrequencyControl(time = 30, count = 5, target = FrequencyControl.Target.UID)
//    @FrequencyControl(time = 60, count = 10, target = FrequencyControl.Target.UID)
    public ApiResult<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, RequestHolder.get().getUid());
        //返回完整消息格式，方便前端展示
        return ApiResult.success(chatService.getMsgResp(msgId, RequestHolder.get().getUid()));
    }

}
