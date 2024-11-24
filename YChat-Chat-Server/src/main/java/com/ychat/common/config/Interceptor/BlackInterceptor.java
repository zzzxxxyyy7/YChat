package com.ychat.common.config.Interceptor;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.ychat.common.Constants.Enums.Impl.BlackTypeEnum;
import com.ychat.common.Constants.Enums.Impl.HttpErrorEnum;
import com.ychat.common.Constants.front.Request.RequestInfo;
import com.ychat.common.user.service.cache.UserCache;
import com.ychat.common.utils.Request.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * 提取 Token 拦截器
 */
@Order(2)
@Component
@Slf4j
public class BlackInterceptor implements HandlerInterceptor {

    @Autowired
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();
        boolean isBlackUid = isBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()));
        if (isBlackUid) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        boolean isBlackIp = isBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.IP.getType()));
        if (isBlackIp) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean isBlackList(Object target, Set<String> set) {
        if (ObjectUtils.isNull(target) || CollectionUtils.isEmpty(set)) {
            return false;
        }
        return set.contains(target.toString());
    }

}
