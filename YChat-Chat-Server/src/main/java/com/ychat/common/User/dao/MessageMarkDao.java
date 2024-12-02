package com.ychat.common.User.Dao;

import com.ychat.common.Constants.Enums.Impl.NormalOrNoEnum;
import com.ychat.common.User.Domain.entity.MessageMark;
import com.ychat.common.User.Mapper.MessageMarkMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 消息标记表 服务实现类
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public List<MessageMark> getValidMarkByMsgIdBatch(List<Long> msgIds) {
        return lambdaQuery()
                .in(MessageMark::getMsgId, msgIds)
                .eq(MessageMark::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .list();
    }

    /**
     * 获取给定的消息标记记录
     * @param uid
     * @param msgId
     * @param markType
     * @return
     */
    public MessageMark get(Long uid, Long msgId, Integer markType) {
        return lambdaQuery().eq(MessageMark::getUid, uid)
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, markType)
                .one();
    }

    /**
     * 查询这条消息被标记的次数
     * @param msgId
     * @param markType
     * @return
     */
    public Integer getMarkCount(Long msgId, Integer markType) {
        return lambdaQuery().eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType, markType)
                .eq(MessageMark::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .count();
    }

}
