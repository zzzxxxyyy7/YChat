package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.MessageMark;
import com.ychat.common.user.mapper.MessageMarkMapper;
import com.ychat.common.user.service.IMessageMarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息标记表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-11-23
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

}
