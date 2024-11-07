package com.ychat.common.user.config.ThreadPool;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * 装饰器模式实现：增强 ThreadFactory
 */
@AllArgsConstructor
public class YChatThreadFactory implements ThreadFactory {

    private static final YChatUncaughtExceptionHandler Y_CHAT_UNCAUGHT_EXCEPTION_HANDLER = new YChatUncaughtExceptionHandler();

    // 组合方式实现装饰器模式
    private ThreadFactory original;

    @Override
    public Thread newThread(Runnable r) {
        // 复用原 Factory 方法创建 Thread ， 省去自己额外实现
        Thread thread = original.newThread(r);
        thread.setUncaughtExceptionHandler(Y_CHAT_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
