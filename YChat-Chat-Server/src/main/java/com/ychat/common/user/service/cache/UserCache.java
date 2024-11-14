package com.ychat.common.user.service.cache;

import com.ychat.common.user.dao.BlackDao;
import com.ychat.common.user.dao.RoleDao;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.UserRole;
import com.ychat.common.user.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
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
    private RoleDao roleDao;

    @Autowired
    private IUserRoleService userRoleService;

    @Cacheable(cacheNames = "user", key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleService.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }
}
