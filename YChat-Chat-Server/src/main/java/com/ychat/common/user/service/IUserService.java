package com.ychat.common.user.service;

import com.ychat.common.user.domain.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
public interface IUserService {

    /**
     * 用户注册，需要获得id
     *
     * @param user
     */
    Long register(User user);
}
