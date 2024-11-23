package com.ychat.common.user.dao;

import com.ychat.common.Constants.Enums.Impl.NormalOrNoEnum;
import com.ychat.common.user.domain.entity.MessageMark;
import com.ychat.common.user.mapper.MessageMarkMapper;
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
