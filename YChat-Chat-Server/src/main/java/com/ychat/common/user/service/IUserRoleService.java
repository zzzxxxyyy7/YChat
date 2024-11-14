package com.ychat.common.user.service;

import com.ychat.common.user.domain.entity.UserRole;

import java.util.List;

/**
 * <p>
 * 用户角色关系表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
public interface IUserRoleService {

    List<UserRole> listByUid(Long uid);
}
