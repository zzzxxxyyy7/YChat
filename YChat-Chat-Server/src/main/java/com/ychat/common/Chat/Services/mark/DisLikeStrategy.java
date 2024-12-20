package com.ychat.common.Chat.Services.mark;


import com.ychat.common.Constants.Enums.Impl.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 消息点踩标记策略类
 */
@Component
public class DisLikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        // 操作父类完成点踩记录生成，消息类型转换
        super.doMark(uid, msgId);
        // 子类同时取消点赞的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
    }

}
