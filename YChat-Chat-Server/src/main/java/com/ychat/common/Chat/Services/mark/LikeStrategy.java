package com.ychat.common.Chat.Services.mark;

import com.ychat.common.Constants.Enums.Impl.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 点赞标记策略类
 */
@Component
public class LikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.LIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        // 操作父类完成点赞记录生成，消息类型转换
        super.doMark(uid, msgId);
        // 子类同时取消点踩的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.DISLIKE.getType()).unMark(uid, msgId);
    }

}
