package com.ychat.common.User.Dao;

import com.ychat.common.Constants.Enums.Impl.NormalOrNoEnum;
import com.ychat.common.User.Domain.entity.MessageMark;
import com.ychat.common.User.Mapper.MessageMarkMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息标记表 服务实现类
 *
 * @author ${author}
 * @since 2024-11-23
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public List<MessageMark> getValidMarkByMsgIdBatch(List<Long> msgIds) {
        return lambdaQuery()
                .in(MessageMark::getMsgId, msgIds)
                .eq(MessageMark::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .list();
    }

}
