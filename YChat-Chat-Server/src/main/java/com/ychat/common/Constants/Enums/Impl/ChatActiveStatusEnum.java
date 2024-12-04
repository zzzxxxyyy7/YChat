package com.ychat.common.Constants.Enums.Impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户是否在线枚举
 */
@AllArgsConstructor
@Getter
public enum ChatActiveStatusEnum {
    ONLINE(1, "在线"),
    OFFLINE(2, "离线"),
    ;

    private final Integer status;
    private final String desc;

    private static Map<Integer, ChatActiveStatusEnum> cache;

    static {
        cache = Arrays.stream(ChatActiveStatusEnum.values()).collect(Collectors.toMap(ChatActiveStatusEnum::getStatus, Function.identity()));
    }

    public static ChatActiveStatusEnum of(Integer type) {
        return cache.get(type);
    }

}
