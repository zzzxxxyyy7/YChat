package com.ychat.common.Utils.Chat;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.ychat.common.Constants.Enums.Impl.ChatActiveStatusEnum;

/**
 * 成员列表工具类
 */
public class ChatMemberHelper {
    // 组合游标分隔符
    private static final String SEPARATOR = "_";

    public static Pair<ChatActiveStatusEnum, String> getCursorPair(String cursor) {
        ChatActiveStatusEnum activeStatusEnum = ChatActiveStatusEnum.ONLINE;
        String timeCursor = null;
        // 如果游标非空
        if (StrUtil.isNotBlank(cursor)) {
            // 解析第一位为筛选状态
            String activeStr = cursor.split(SEPARATOR)[0];
            // 解析第二位为时间戳
            String timeStr = cursor.split(SEPARATOR)[1];
            activeStatusEnum = ChatActiveStatusEnum.of(Integer.parseInt(activeStr));
            timeCursor = timeStr;
        }
        return Pair.of(activeStatusEnum, timeCursor);
    }

    public static String generateCursor(ChatActiveStatusEnum activeStatusEnum, String timeCursor) {
        return activeStatusEnum.getStatus() + SEPARATOR + timeCursor;
    }

}
