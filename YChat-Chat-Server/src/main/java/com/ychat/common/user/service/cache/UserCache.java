package com.ychat.common.user.service.cache;

import com.ychat.common.user.dao.BlackDao;
import com.ychat.common.user.domain.entity.Black;
import com.ychat.common.user.domain.entity.UserRole;
import com.ychat.common.user.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 用户相关缓存
 * 当用户登录后，缓存相关用户数据
 */
@Component
public class UserCache {

    @Autowired
    private BlackDao blackDao;

    @Autowired
    private IUserRoleService userRoleService;

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
}
