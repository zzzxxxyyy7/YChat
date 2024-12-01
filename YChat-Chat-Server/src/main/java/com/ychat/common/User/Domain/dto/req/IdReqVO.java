package com.ychat.common.User.Domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 删除表情包请求类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReqVO {

    @ApiModelProperty("id")
    @NotNull
    private long id;

}
