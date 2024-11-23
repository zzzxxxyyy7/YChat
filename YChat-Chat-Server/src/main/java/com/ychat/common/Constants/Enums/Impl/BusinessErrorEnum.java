package com.ychat.common.Constants.Enums.Impl;

import com.ychat.common.Constants.Enums.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 业务校验异常码
 */
@AllArgsConstructor
@Getter
public enum BusinessErrorEnum implements ErrorEnum {
    //==================================common==================================
    BUSINESS_ERROR(1001, "{0}"),
    //==================================user==================================
    //==================================chat==================================
    SYSTEM_ERROR(1001, "系统出小差了，请稍后再试"),
    ;
    private Integer code;
    private String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

}
