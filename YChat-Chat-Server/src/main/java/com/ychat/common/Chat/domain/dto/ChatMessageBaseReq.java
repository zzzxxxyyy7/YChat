package com.ychat.common.Chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * Description: 消息基础请求体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageBaseReq {

    @NotNull
    @ApiModelProperty("需要被撤回的消息 ID")
    private Long msgId;

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;

}
