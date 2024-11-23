package com.ychat.common.Constants.Enums.Impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum YesOrNoEnum {

    NO(0 , "否"),
    YES(1 , "是"),
    ;

    private final Integer status;

    private final String desc;

    public static Integer toStatus(Boolean bool) {
        return bool ? YES.getStatus() : NO.getStatus();
    }

}
