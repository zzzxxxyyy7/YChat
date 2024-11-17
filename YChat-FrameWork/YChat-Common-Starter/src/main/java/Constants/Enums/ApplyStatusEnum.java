package Constants.Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description : 好友申请申请状态枚举
 */
@Getter
@AllArgsConstructor
public enum ApplyStatusEnum {

    WAIT_APPROVAL(1, "待审批"),

    AGREE(2, "同意");

    private final Integer code;

    private final String desc;
}
