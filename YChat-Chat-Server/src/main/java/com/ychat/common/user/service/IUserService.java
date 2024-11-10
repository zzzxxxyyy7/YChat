package com.ychat.common.user.service;

import com.ychat.common.user.domain.dto.ModifyNameReq;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.vo.BadgeResp;
import com.ychat.common.user.domain.vo.UserInfoVo;

import java.util.List;

/**
 * 用户表 服务类
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

    /**
     * 获取徽章列表
     * @param uid
     * @return
     */
    List<BadgeResp> badges(Long uid);
}
