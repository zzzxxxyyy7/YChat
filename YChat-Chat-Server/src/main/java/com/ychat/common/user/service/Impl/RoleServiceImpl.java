package com.ychat.common.user.service.Impl;

import com.ychat.common.Enums.RoleEnum;
import com.ychat.common.user.service.IRoleService;
import com.ychat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private UserCache userCache;

    @Override
    public boolean hasRole(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return roleSet.contains(roleEnum.getId());
    }

    @Override
    public boolean isAdmin(Set<Long> roleSet) {
        if (roleSet.isEmpty()) return false;
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }

}
