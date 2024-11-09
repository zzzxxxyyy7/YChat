package com.ychat.common.user.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVo {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 性别 1为男性，2为女性
     */
    private Integer sex;

    /**
     * 微信openid用户标识
     */
    private String openId;

    /**
     * 在线状态 1在线 2离线
     */
    private Integer activeStatus;

    /**
     * 最后上下线时间
     */
    private LocalDateTime lastOptTime;

    /**
     * ip信息
     */
    private String ipInfo;

    /**
     * 佩戴的徽章id
     */
    private Long itemId;
}
