package com.ychat.common.Chat.domain.dto;

import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 消息已读未读 Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadReq extends CursorPageBaseReq {

    @ApiModelProperty("消息id")
    @NotNull
    private Long msgId;

    @ApiModelProperty("查询类型 1已读 2未读")
    @NotNull
    private Long searchType;

}
