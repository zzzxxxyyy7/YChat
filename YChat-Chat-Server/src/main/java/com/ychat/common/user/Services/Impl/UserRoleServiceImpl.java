package com.ychat.common.User.Services.Impl;

import com.ychat.common.User.Dao.UserRoleDao;
import com.ychat.common.User.Domain.entity.UserRole;
import com.ychat.common.User.Services.IUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserRoleServiceImpl implements IUserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public List<UserRole> listByUid(Long uid) {
        return userRoleDao.listByUid(uid);
    }
}
