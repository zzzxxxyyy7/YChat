package com.ychat.common.chat.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 推送消息目标范围枚举类
 */
@AllArgsConstructor
@Getter
public enum WSPushTypeEnum {
    USER(1, "个人"),
    ALL(2, "全部连接用户"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSPushTypeEnum> cache;

    static {
        cache = Arrays.stream(WSPushTypeEnum.values()).collect(Collectors.toMap(WSPushTypeEnum::getType, Function.identity()));
    }

    public static WSPushTypeEnum of(Integer type) {
        return cache.get(type);
    }

}
