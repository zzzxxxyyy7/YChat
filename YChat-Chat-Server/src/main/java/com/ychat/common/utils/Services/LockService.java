package com.ychat.common.utils.Services;

import com.ychat.common.Exception.BusinessException;
import com.ychat.common.Exception.CommonErrorEnum;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class LockService {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * Function 接收入参返回出参
     * Supplier 不需要入参，只返回出参
     * @param key
     * @param waitTime
     * @param leaseTime
     * @param unit
     * @param <T>
     */
    @SneakyThrows
    public <T> T executeWithLock(String key , int waitTime, int leaseTime, TimeUnit unit, Supplier<T> supplier) {
        RLock lock = redissonClient.getLock(key);
        // 将 tryLock 移出 try 块，这是为了避免 tryLock 获取锁失败导致执行 finally ，但是实际上没有锁反而释放导致的异常
        boolean isLock = lock.tryLock(waitTime, leaseTime, unit);
        if (!isLock) {
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();
        } finally {
            // 确保在操作结束后释放锁
            lock.unlock();
        }
    }
}
