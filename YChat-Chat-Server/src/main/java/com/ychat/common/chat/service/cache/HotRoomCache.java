package com.ychat.common.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.ychat.Utils.Redis.RedisUtils;
import com.ychat.common.Constants.front.Request.CursorPageBaseReq;
import com.ychat.common.config.Redis.RedisKeyBuilder;
import com.ychat.common.utils.Request.CursorPageBaseResp;
import com.ychat.common.utils.Request.CursorUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * Description: 全局房间
 */
@Component
public class HotRoomCache {

    /**
     * 获取热门群聊翻页
     * @return
     */
    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKeyBuilder.getKey(RedisKeyBuilder.HOT_ROOM_ZET), Long::parseLong);
    }

    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart, Double hotEnd) {
        return RedisUtils.zRangeByScoreWithScores(RedisKeyBuilder.getKey(RedisKeyBuilder.HOT_ROOM_ZET), hotStart, hotEnd);
    }

    /**
     * 更新热门群聊的最新时间
     */
    public void refreshActiveTime(Long roomId, Date refreshTime) {
        RedisUtils.zAdd(RedisKeyBuilder.getKey(RedisKeyBuilder.HOT_ROOM_ZET), roomId, (double) refreshTime.getTime());
    }

}
