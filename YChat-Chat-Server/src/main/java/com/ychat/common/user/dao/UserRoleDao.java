package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.UserRole;
import com.ychat.common.user.mapper.UserRoleMapper;
import com.ychat.common.user.service.IUserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色关系表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

}
