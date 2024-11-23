package com.ychat.common.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import com.ychat.common.config.Redis.RedisKeyBuilder;
import com.ychat.common.user.dao.BlackDao;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.Black;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.entity.UserRole;
import com.ychat.common.user.service.IUserRoleService;
import Utils.Redis.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 用户相关缓存
 * 当用户登录后，缓存相关用户数据
 */
@Component
public class UserCache {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BlackDao blackDao;

    @Autowired
    private IUserRoleService userRoleService;

    @Autowired
    private UserSummaryCache userSummaryCache;

    @Cacheable(cacheNames = "userRole", key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleService.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }

    /**
     * 拉黑目标缓存列表
     * @return
     */
    @Cacheable(cacheNames = "blackTarget", key = "'black'+#uid")
    public Map<Integer, Set<String>> getBlackMap() {
        // 拿到 Type 对应的缓存列表
        Map<Integer, List<Black>> blackMap = blackDao.list().stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>();
        // List 转 Set
        blackMap.forEach((type, list) -> {
            result.put(type, list.stream().map(Black::getTarget).collect(Collectors.toSet()));
        });
        return result;
    }

    /**
     * 清空拉黑目标缓存
     * @return
     */
    @CacheEvict(cacheNames = "blackTarget", key = "'black'+#uid")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    /**
     * 统一刷新用户信息
     * @param uid
     */
    public void userInfoChange(Long uid) {
        delUserInfo(uid);
        //  删除UserSummaryCache，前端下次懒加载的时候可以获取到最新的数据
        userSummaryCache.delete(uid);
        refreshUserModifyTime(uid);
    }

    /**
     * 删除用户属性的本地缓存
     * @param uid
     */
    public void delUserInfo(Long uid) {
        String key = RedisKeyBuilder.getKey(RedisKeyBuilder.USER_INFO_STRING, uid);
        RedisUtils.del(key);
    }

    /**
     * 拿到用户属性最近的修改时间
     * @param uidList
     * @return
     */
    public List<Long> getUserModifyTime(List<Long> uidList) {
        List<String> keys = uidList.stream().map(uid -> RedisKeyBuilder.getKey(RedisKeyBuilder.USER_INFO_MODIFY_STRING, uid)).collect(Collectors.toList());
        return RedisUtils.mget(keys, Long.class);
    }

    /**
     * 刷新用户属性的最近修改时间
     * @param uid
     */
    public void refreshUserModifyTime(Long uid) {
        String key = RedisKeyBuilder.getKey(RedisKeyBuilder.USER_INFO_MODIFY_STRING, uid);
        RedisUtils.set(key, new Date().getTime());
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid) {//todo 后期做二级缓存
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        //批量组装key
        List<String> keys = uids.stream().map(a -> RedisKeyBuilder.getKey(RedisKeyBuilder.USER_INFO_STRING, a)).collect(Collectors.toList());
        //批量get
        List<User> mget = RedisUtils.mget(keys, User.class);
        Map<Long, User> map = mget.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getId, Function.identity()));
        //发现差集——还需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !map.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            //批量load
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a -> RedisKeyBuilder.getKey(RedisKeyBuilder.USER_INFO_STRING, a.getId()), Function.identity()));
            RedisUtils.mset(redisMap, 5 * 60);
            //加载回redis
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return map;
    }

}
