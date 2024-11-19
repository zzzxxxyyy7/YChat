package com.ychat.common.user.domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description: 拉黑目标
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackReq {

    @ApiModelProperty("拉黑目标uid")
    private Long uid;

    @ApiModelProperty("拉黑目标ip")
    private String ip;
}
