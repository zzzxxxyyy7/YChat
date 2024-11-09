package com.ychat.common.user.service.Impl;

import com.ychat.common.config.Redis.RedisKeyBuilder;
import com.ychat.common.user.service.LoginService;
import com.ychat.common.utils.jwt.JwtUtils;
import com.ychat.common.utils.redis.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    private static final int TOKEN_EXPIRE_DAYS = 3;
    private static final int RENEW_TOKEN_EXPIRE_DAYS = 1;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 续期 token
     * @param token
     */
    @Override
    @Async
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String userTokenKey = getUserTokenKey(uid);
        Long expire = RedisUtils.getExpire(userTokenKey , TimeUnit.DAYS);
        // 小于特定天数，对 token 续期
        if (expire == -2) return ;
        if (expire < RENEW_TOKEN_EXPIRE_DAYS) {
            RedisUtils.expire(getUserTokenKey(uid) , TOKEN_EXPIRE_DAYS , TimeUnit.DAYS);
        }

    }

    /**
     * 登录时获取 token
     * @param uid
     * @return
     */
    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 校验 Token 是否合法
     * @param token
     * @return
     */
    @Override
    public Long getValidUid(String token) {

        Long uid = jwtUtils.getUidOrNull(token);

        // token 中拿不到 uid，非法 token
        if (null == uid) return null;

        // 查询 token 是否在缓存中
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));

        // 如果不在，说明过期
        if (StringUtils.isEmpty(oldToken)) return null;

        // 返回 uid，新旧 token 必须一样
        return Objects.equals(token , oldToken) ? uid : null;
    }

    /**
     * 通过 uid 获取 token Key
     * @param uid
     * @return
     */
    public String getUserTokenKey(Long uid) {
        // 构建 Redis Key
        return RedisKeyBuilder.getKey(RedisKeyBuilder.USER_TOKEN_STRING , uid);
    }
}
