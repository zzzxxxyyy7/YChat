package com.ychat.common.Config.ThreadPool;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义未被捕获异常拦截器
 */
@Slf4j
public class YChatUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 抛出未被捕获异常时逻辑
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception In Thread: {} , {}", t.getName(), e.getMessage());
    }
}
