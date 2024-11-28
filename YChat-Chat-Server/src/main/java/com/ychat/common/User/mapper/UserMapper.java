package com.ychat.common.User.Mapper;

import com.ychat.common.User.Domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
