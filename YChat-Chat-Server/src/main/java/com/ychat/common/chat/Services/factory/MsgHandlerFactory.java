package com.ychat.common.Chat.Services.factory;

import com.ychat.common.Constants.Enums.Impl.CommonErrorEnum;
import com.ychat.common.Utils.Assert.AssertUtil;
import com.ychat.common.Chat.Services.handler.AbstractMsgHandler;

import java.util.HashMap;
import java.util.Map;

public class MsgHandlerFactory {

    private static final Map<Integer, AbstractMsgHandler<?>> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code, AbstractMsgHandler<?> strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractMsgHandler<?> getStrategyNoNull(Integer code) {
        AbstractMsgHandler<?> strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }

}
