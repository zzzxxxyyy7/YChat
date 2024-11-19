package com.ychat.common.user.domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WearingBadgeReq {

    @NotNull
    @ApiModelProperty("徽章ID")
    private Long itemId;
}
