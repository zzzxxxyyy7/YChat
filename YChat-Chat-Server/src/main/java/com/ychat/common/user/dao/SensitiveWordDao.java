package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.SensitiveWord;
import com.ychat.common.user.mapper.SensitiveWordMapper;
import com.ychat.common.user.service.ISensitiveWordService;
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
