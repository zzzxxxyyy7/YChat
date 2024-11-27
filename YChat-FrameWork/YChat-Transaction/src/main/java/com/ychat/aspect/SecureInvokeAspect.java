package com.ychat.aspect;

import cn.hutool.core.date.DateUtil;
import com.ychat.Utils.Redis.JsonUtils;
import com.ychat.annotation.SecureInvoke;
import com.ychat.domain.dto.SecureInvokeDTO;
import com.ychat.domain.entity.SecureInvokeRecord;
import com.ychat.service.SecureInvokeHolder;
import com.ychat.service.SecureInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 安全执行切面
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 确保这个切面最先执行
@Component
public class SecureInvokeAspect {

    @Autowired
    private SecureInvokeService secureInvokeService;

    @Around("@annotation(secureInvoke)")
    public Object around(ProceedingJoinPoint joinPoint, SecureInvoke secureInvoke) throws Throwable {
        boolean async = secureInvoke.async();

        // 拿到这个方法的执行是否在事务内（表明这俩个操作是否是分布式事务）
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();

        // 非事务状态，直接执行，不做任何保证
        if (SecureInvokeHolder.isInvoking() || !inTransaction) {
            return joinPoint.proceed();
        }

        // 拿到切面所拦截的方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        // 拿到方法的参数类型
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());

        SecureInvokeDTO dto = SecureInvokeDTO.builder()
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameters))
                .build();

        SecureInvokeRecord record = SecureInvokeRecord.builder()
                .secureInvokeDTO(dto)
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .build();

        // 切面封装好方法调用参数，交给对应服务类判断是异步还是同步执行
        secureInvokeService.invoke(record, async);
        return null;
    }

}
