package com.ychat.common.user.mapper;

import com.ychat.common.user.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    void modifyName(Long uid, String name);
}
