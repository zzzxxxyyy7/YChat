package com.ychat.common.Constants.Enums.Impl;

import com.ychat.common.Constants.Enums.ErrorEnum;
import com.ychat.common.Constants.front.Response.ApiResult;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Description: 业务校验异常码
 */
@AllArgsConstructor
@Getter
public enum HttpErrorEnum implements ErrorEnum {
    ACCESS_DENIED(401, "登录失效，请重新登录"),
    ;
    private Integer httpCode;
    private String msg;

    @Override
    public Integer getErrorCode() {
        return httpCode;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }

    public void sendHttpError(HttpServletResponse response) throws IOException {
        response.setStatus(this.getErrorCode());
        ApiResult<?> responseData = ApiResult.fail(this);
        response.setContentType(ContentType.JSON.toString(Charset.forName("UTF-8")));
        response.getWriter().write(JSONUtil.toJsonStr(responseData));
    }
}
