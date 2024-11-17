package Constants.Enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum YesOrNoEnum {
    NO(0 , "否"),
    YES(1 , "是"),
    ;

    private final Integer status;

    private final String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
