package com.ychat.common.User.Domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoVo {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "剩余改名次数")
    private Integer modifyNameChance;
}
