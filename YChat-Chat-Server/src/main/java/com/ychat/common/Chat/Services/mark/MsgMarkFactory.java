package com.ychat.common.Chat.Services.mark;


import com.ychat.common.Constants.Enums.Impl.CommonErrorEnum;
import com.ychat.common.Utils.Assert.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息标记策略工厂
 */
public class MsgMarkFactory {

    // 消息标记策略类集合
    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer markType, AbstractMsgMarkStrategy strategy) {
        STRATEGY_MAP.put(markType, strategy);
    }

    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType) {
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }

}
