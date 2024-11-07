package com.ychat.common.user.controller;

import com.ychat.common.user.config.ThreadPool.YChatUncaughtExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ychat.common.user.config.ThreadPool.ThreadPoolConfig.YCHAT_EXECUTOR;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier(YCHAT_EXECUTOR)
    private ThreadPoolTaskExecutor executor;

    @RequestMapping("/redissonClient")
    public void redissonClientTest() {
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println(5465);
        lock.unlock();
    }

    @RequestMapping("/redis")
    public void redis() {
        redisTemplate.opsForValue().set("name","卷心菜");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name); //卷心菜
    }

    @RequestMapping("/thread")
    public void thread() throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("123");
            throw new RuntimeException("错误");
        });
        thread.setUncaughtExceptionHandler(new YChatUncaughtExceptionHandler());

        thread.start();
        Thread.sleep(200);
    }

    @RequestMapping("/threads")
    public void threads() throws InterruptedException {
        executor.execute(() -> {
            log.info("123");
            throw new RuntimeException("错误");
        });
        Thread.sleep(200);
    }
}
