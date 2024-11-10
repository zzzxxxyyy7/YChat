package com.ychat.common.Exception;

import com.ychat.common.front.Response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 将捕获到的 BusinessException 异常转换为 ApiResult
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        log.info("Business Exception, The Reason is {}", e.getMessage());
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 将捕获到的 MethodArgumentNotValidException 异常转换为 ApiResult
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(error -> errorMessage.append(error.getField()).append(error.getDefaultMessage()).append(" And "));
        String errorMsg = errorMessage.toString();
        return ApiResult.fail(CommonErrorEnum.PARAM_INVALID.getCode() , errorMsg.substring(0 , errorMsg.length() - 5));
    }

    @ExceptionHandler(value = Throwable.class)
    public ApiResult<Void> handleThrowable(Throwable e) {
        log.error("System Exception, The Reason is {}", e.getMessage() , e);
        return ApiResult.fail(CommonErrorEnum.SYSTEM_ERROR);
    }
}
