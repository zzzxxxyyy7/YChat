package com.ychat.common.user.service;

import com.ychat.common.Constants.Enums.Impl.RoleEnum;

import java.util.Set;

/**
 * 角色表 服务类
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
public interface IRoleService  {

    /**
     * 是否有某个权限，临时做法
     *
     * @return
     */
    boolean hasRole(Long uid, RoleEnum roleEnum);

    /**
     * 是否是管理员
     *
     * @param roleSet
     * @return
     */
    boolean isAdmin(Set<Long> roleSet);
}
