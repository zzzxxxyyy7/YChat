package com.ychat.common.Chat.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息已读未读 VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgReadInfoDTO {

    @ApiModelProperty("消息id")
    private Long msgId;

    @ApiModelProperty("已读数")
    private Integer readCount;

    @ApiModelProperty("未读数")
    private Integer unReadCount;

}
