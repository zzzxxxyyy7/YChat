package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.User.Domain.entity.Message;
import com.ychat.common.User.Mapper.MessageMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2024-11-18
 */
@Service
public class MessageDao extends ServiceImpl<MessageMapper, Message> {

    public Integer getGapCount(Long roomId, Long fromId, Long toId) {
        return lambdaQuery()
                .eq(Message::getRoomId, roomId)
                .gt(Message::getId, fromId)
                .le(Message::getId, toId)
                .count();
    }


}
