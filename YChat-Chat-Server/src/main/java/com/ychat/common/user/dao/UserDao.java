package com.ychat.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    @Autowired
    private UserMapper userMapper;

    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getOpenId, openId);
        return getOne(wrapper);
    }

    public User getByName(String Name) {
        return lambdaQuery().eq(User::getName, Name).one();
    }

    public void modifyName(Long uid, String name) {
        userMapper.modifyName(uid, name);
    }
}
