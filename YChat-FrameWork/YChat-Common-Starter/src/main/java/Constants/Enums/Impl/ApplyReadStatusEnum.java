package Constants.Enums.Impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description : 好友申请记录阅读状态枚举
 */
@Getter
@AllArgsConstructor
public enum ApplyReadStatusEnum {

    UNREAD(1, "未读"),

    READ(2, "已读");

    private final Integer code;

    private final String desc;
}
