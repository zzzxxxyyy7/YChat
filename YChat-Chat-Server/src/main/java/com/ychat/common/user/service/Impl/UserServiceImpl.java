package com.ychat.common.user.service.Impl;

import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Rhss
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-11-05 06:04:49
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public Long register(User newUser) {
        userDao.save(newUser);
        // TODO 注册用户通知

        return newUser.getId();
    }
}






