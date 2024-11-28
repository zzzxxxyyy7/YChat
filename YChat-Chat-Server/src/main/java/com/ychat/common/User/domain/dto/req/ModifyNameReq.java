package com.ychat.common.User.Domain.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * Description: 修改用户名
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyNameReq {

    @NotNull
    @Length(max = 6, message = "用户名不能大于 6 位")
    @ApiModelProperty("用户名")
    private String name;

}
