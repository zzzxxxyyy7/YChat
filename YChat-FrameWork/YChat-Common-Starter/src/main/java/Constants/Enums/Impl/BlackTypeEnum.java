package Constants.Enums.Impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 黑名单类型
 */
@AllArgsConstructor
@Getter
public enum BlackTypeEnum {
    IP(1),
    UID(2),
    ;

    private final Integer type;

}
