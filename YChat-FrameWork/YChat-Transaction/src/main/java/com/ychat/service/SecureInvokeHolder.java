package com.ychat.service;

import java.util.Objects;

/**
 * 用来标识线程此时执行的方法是否在本地执行
 */
public class SecureInvokeHolder {

    private static final ThreadLocal<Boolean> INVOKE_THREAD_LOCAL = new ThreadLocal<>();

    public static boolean isInvoking() {
        return Objects.nonNull(INVOKE_THREAD_LOCAL.get());
    }

    public static void setInvoking() {
        INVOKE_THREAD_LOCAL.set(Boolean.TRUE);
    }

    public static void invoked() {
        INVOKE_THREAD_LOCAL.remove();
    }

}
