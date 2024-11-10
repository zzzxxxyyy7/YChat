package com.ychat.common.user.service;

import com.ychat.common.user.domain.dto.ModifyNameReq;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.vo.UserInfoVo;

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
     * @param user
     */
    Long register(User user);

    /**
     * 获取用户信息
     * @param uid
     * @return
     */
    UserInfoVo getUserInfo(Long uid);

    /**
     * 修改用户名
     *
     * @param uid
     * @param req
     */
    void modifyName(Long uid, ModifyNameReq req);

}
