package com.ychat.common.Chat.domain.dto;

import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Description: 消息记录列表请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessagePageReq extends CursorPageBaseReq {

    @NotNull
    @ApiModelProperty("会话id")
    private Long roomId;

}
