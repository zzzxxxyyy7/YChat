package com.ychat.common.Chat.domain.dto;

import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MemberReq extends CursorPageBaseReq {

    @ApiModelProperty("会话ID")
    private Long roomId = 1L;

}
