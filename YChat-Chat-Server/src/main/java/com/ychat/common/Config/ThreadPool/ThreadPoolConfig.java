package com.ychat.common.Config.ThreadPool;

import com.ychat.annotation.SecureInvokeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description: 项目统一线程池配置
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig implements AsyncConfigurer , SecureInvokeConfigurer {

    /**
     * 项目共用线程池
     */
    public static final String YCHAT_EXECUTOR = "yChatExecutor";

    /**
     * 推送 WebSocket 消息专用线程池
     */
    public static final String WS_EXECUTOR = "wsExecutor";

    /**
     * 推送 WebSocket 消息专用线程池
     */
    public static final String Transaction_EXECUTOR = "transactionExecutor";

    @Override
    public Executor getAsyncExecutor() {
        return yChatExecutor();
    }

    @Override
    public Executor getSecureInvokeExecutor() {
        return yChatExecutor();
    }

    @Bean(YCHAT_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor yChatExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池优雅停机，Bean Destory 的时候会被 Spring 回调，执行最后的逻辑处理再退出
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("ychat-executor-");
        // 满了调用线程执行，认为重要任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置线程工厂执行异常捕获
        executor.setThreadFactory(new YChatThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    @Bean(WS_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor wsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池优雅停机，Bean Destory 的时候会被 Spring 回调，执行最后的逻辑处理再退出
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("websocket-executor-");
        // 满了调用线程执行，认为重要任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 设置线程工厂执行异常捕获
        executor.setThreadFactory(new YChatThreadFactory(executor));
        executor.initialize();
        return executor;
    }

    @Bean(Transaction_EXECUTOR)
    @Primary
    public ThreadPoolTaskExecutor transactionEXECUTOR() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池优雅停机，Bean Destory 的时候会被 Spring 回调，执行最后的逻辑处理再退出
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(400);
        executor.setThreadNamePrefix("websocket-executor-");
        // 满了调用线程执行，认为重要任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 设置线程工厂执行异常捕获
        executor.setThreadFactory(new YChatThreadFactory(executor));
        executor.initialize();
        return executor;
    }

}
