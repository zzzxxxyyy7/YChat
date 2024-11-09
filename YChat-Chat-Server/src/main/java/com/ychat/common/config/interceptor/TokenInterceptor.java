package com.ychat.common.config.interceptor;

import com.ychat.common.Error.HttpErrorEnum;
import com.ychat.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
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
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String UID = "uid";

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        // 如果 token 无效
        if (Objects.isNull(validUid)) {
            // 校验接口是否需要登录
            boolean privateURI = isPrivateURI(request, response);
            if (privateURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
                return false;
            }
        }
        return true;
    }

    /**
     * 如果为 private，表示必须登录才可以访问，返回 false
     * @param request
     * @return
     */
    private boolean isPrivateURI(HttpServletRequest request , HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        String[] URIArray = requestURI.split("/");
        List<String> URIList = Arrays.stream(URIArray).collect(Collectors.toList());
        return URIList.contains("private");
    }

    private String getToken(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(TOKEN_HEADER))
                .filter(h -> h.startsWith(BEARER))
                .map(h -> h.replaceFirst(BEARER, ""))
                .orElse(null);
        return token;
    }


}
