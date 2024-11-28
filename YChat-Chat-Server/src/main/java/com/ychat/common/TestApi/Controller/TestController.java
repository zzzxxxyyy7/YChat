package com.ychat.common.TestApi.Controller;

import com.ychat.common.Constants.front.Response.ApiResult;
import com.ychat.common.Config.Redis.RedisKeyBuilder;
import com.ychat.common.Config.ThreadPool.YChatUncaughtExceptionHandler;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.Utils.Jwt.JwtUtils;
import com.ychat.Utils.Redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.ychat.common.Config.ThreadPool.ThreadPoolConfig.YCHAT_EXECUTOR;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    @Qualifier(YCHAT_EXECUTOR)
    private ThreadPoolTaskExecutor executor;

    @GetMapping("/user")
    public ApiResult<?> userTest() {
        return ApiResult.success(userDao.getById("11000"));
    }

    @GetMapping("/redissonClient")
    public void redissonClientTest() {
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println(5465);
        lock.unlock();
    }

    @GetMapping("/redis")
    public void redis() {
        redisTemplate.opsForValue().set("name","卷心菜");
        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name); //卷心菜
    }

    @GetMapping("/thread")
    public void thread() throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("123");
            throw new RuntimeException("错误");
        });
        thread.setUncaughtExceptionHandler(new YChatUncaughtExceptionHandler());

        thread.start();
        Thread.sleep(200);
    }

    @GetMapping("/threads")
    public void threads() throws InterruptedException {
        executor.execute(() -> {
            log.info("123");
            throw new RuntimeException("错误");
        });
        Thread.sleep(200);
    }

    @GetMapping("/jwt")
    public String getUserTokenKey(Long uid) {
        String token = jwtUtils.createToken(uid);
        String userTokenKey = RedisKeyBuilder.getKey(RedisKeyBuilder.USER_TOKEN_STRING , uid);
        RedisUtils.set(userTokenKey, token, 9999, TimeUnit.DAYS);
        return "永久测试 Token 生成成功: " + token;
    }
}
