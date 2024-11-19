package com.ychat.common.user.domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


/**
 * Description: 好友申请Dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApproveReq {

    @NotNull
    @ApiModelProperty("申请id")
    private Long applyId;

}
