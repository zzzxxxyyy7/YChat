package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.User.Domain.entity.UserRole;
import com.ychat.common.User.Mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色关系表 服务实现类
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
@Service
public class UserRoleDao extends ServiceImpl<UserRoleMapper, UserRole> {

    public List<UserRole> listByUid(Long uid) {
        return lambdaQuery().eq(UserRole::getUid, uid).list();
    }
}
