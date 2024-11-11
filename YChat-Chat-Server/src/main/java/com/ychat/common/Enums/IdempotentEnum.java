package com.ychat.common.Enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 场景枚举类
 */
@AllArgsConstructor
@Getter
public enum IdempotentEnum {
    UID(1, "UID"),
    ITEM_ID(2, "获取物品消息ID"),
    ;

    private final Integer type;
    private final String desc;
}
