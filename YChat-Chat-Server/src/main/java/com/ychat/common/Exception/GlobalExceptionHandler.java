package com.ychat.common.Exception;

import com.ychat.common.front.Response.ApiResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 将捕获到的 BusinessException 异常转换为 ApiResult
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
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
        e.getBindingResult().getFieldErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(";"));
        return null;
    }
}
