package com.ychat.common.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommonErrorEnum implements ErrorEnum {

    SYSTEM_ERROR(-1, "系统出小差了，请稍后再试"),
    FREQUENCY_LIMIT(-3, "请求太频繁了，请稍后再试"),
    LOCK_LIMIT(-4, "请求太频繁了，请稍后再试"),
    PARAM_INVALID(-5, "参数校验失败"),
    BUSINESS_ERROR(0, "业务异常");
    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMsg() {
        return this.msg;
    }
}
