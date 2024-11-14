package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.Role;
import com.ychat.common.user.mapper.RoleMapper;
import com.ychat.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}
