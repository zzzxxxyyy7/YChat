package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.Black;
import com.ychat.common.user.mapper.BlackMapper;
import com.ychat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
