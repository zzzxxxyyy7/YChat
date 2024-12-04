package com.ychat.common.Chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 退出群聊
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberExitReq {

    @NotNull
    @ApiModelProperty("会话ID")
    private Long roomId;

}
