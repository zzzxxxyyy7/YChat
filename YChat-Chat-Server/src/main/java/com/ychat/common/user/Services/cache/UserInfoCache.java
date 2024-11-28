package com.ychat.common.User.Services.cache;

import com.ychat.Utils.Cache.AbstractRedisStringCache;
import com.ychat.common.Config.Redis.RedisKeyBuilder;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 用户基本信息的缓存
 */
@Component
public class UserInfoCache extends AbstractRedisStringCache<Long, User> {

    @Autowired
    private UserDao userDao;

    @Override
    protected String getKey(Long uid) {
        return RedisKeyBuilder.getKey(RedisKeyBuilder.USER_INFO_STRING, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, User> load(List<Long> uidList) {
        List<User> needLoadUserList = userDao.listByIds(uidList);
        return needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
