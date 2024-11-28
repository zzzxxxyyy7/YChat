package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.User.Domain.entity.Role;
import com.ychat.common.User.Mapper.RoleMapper;
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
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
