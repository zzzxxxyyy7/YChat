package com.ychat.common.Constants.Enums.Impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 会话是否正常的通用枚举
 */
@AllArgsConstructor
@Getter
public enum NormalOrNoEnum {
    NORMAL(0, "启用"),
    NOT_NORMAL(1, "禁用"),
    ;

    private final Integer status;
    private final String desc;

    private static Map<Integer, NormalOrNoEnum> cache;

    static {
        cache = Arrays.stream(NormalOrNoEnum.values()).collect(Collectors.toMap(NormalOrNoEnum::getStatus, Function.identity()));
    }

    public static NormalOrNoEnum of(Integer type) {
        return cache.get(type);
    }

    public static Integer toStatus(Boolean bool) {
        return bool ? NORMAL.getStatus() : NOT_NORMAL.getStatus();
    }

}
