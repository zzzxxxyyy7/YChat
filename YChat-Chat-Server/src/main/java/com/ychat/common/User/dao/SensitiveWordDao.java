package com.ychat.common.User.Dao;

import com.ychat.common.User.Domain.entity.SensitiveWord;
import com.ychat.common.User.Mapper.SensitiveWordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 敏感词库 服务实现类
 *
 * @author ${author}
 * @since 2024-11-23
 */
@Service
public class SensitiveWordDao extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {

}
