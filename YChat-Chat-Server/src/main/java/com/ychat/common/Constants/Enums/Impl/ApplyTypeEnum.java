package com.ychat.common.Constants.Enums.Impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description : 申请类型枚举
 */
@Getter
@AllArgsConstructor
public enum ApplyTypeEnum {

    ADD_FRIEND(1, "加好友");

    private final Integer code;

    private final String desc;
}
