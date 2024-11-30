package com.ychat.common.Config.Interceptor;

import com.ychat.common.Constants.Enums.Impl.HttpErrorEnum;
import com.ychat.common.User.Services.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提取 Token 拦截器
 */
@Order(-2)
@Component
@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        // 如果 token 无效
        if (Objects.isNull(validUid)) {
            // 校验接口是否需要登录
            boolean isPublicURI = isPublicURI(request.getRequestURI());
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        request.setAttribute(ATTRIBUTE_UID, validUid);
        return true;
    }

    /**
     * 判断是不是公共方法，可以未登录访问的
     *
     * @param requestURI
     */
    private boolean isPublicURI(String requestURI) {
        String[] split = requestURI.split("/");
        return split.length > 2 && "public".equals(split[3]);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        return Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.replaceFirst(AUTHORIZATION_SCHEMA, ""))
                .orElse(null);
    }


}
