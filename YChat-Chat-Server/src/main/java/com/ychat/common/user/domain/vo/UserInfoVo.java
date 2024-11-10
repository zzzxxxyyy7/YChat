package com.ychat.common.user.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVo {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别 1为男性，2为女性")
    private Integer sex;

    @ApiModelProperty(value = "微信openid用户标识")
    private String openId;

    @ApiModelProperty(value = "在线状态 1在线 2离线")
    private Integer activeStatus;

    @ApiModelProperty(value = "最后上下线时间")
    private LocalDateTime lastOptTime;

    @ApiModelProperty(value = "ip信息")
    private String ipInfo;

    @ApiModelProperty(value = "佩戴的徽章id")
    private Long itemId;

    @ApiModelProperty(value = "剩余改名次数")
    private Integer modifyNameChance;
}
