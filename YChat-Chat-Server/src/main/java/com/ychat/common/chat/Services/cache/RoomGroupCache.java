package com.ychat.common.Chat.Services.cache;


import com.ychat.Utils.Cache.AbstractRedisStringCache;
import com.ychat.common.Config.Redis.RedisKeyBuilder;
import com.ychat.common.User.Dao.RoomGroupDao;
import com.ychat.common.User.Domain.entity.RoomGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 群聊会话基本信息的缓存
 */
@Component
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {

    @Autowired
    private RoomGroupDao roomGroupDao;

    @Override
    protected String getKey(Long roomId) {
        return RedisKeyBuilder.getKey(RedisKeyBuilder.GROUP_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> roomIds) {
        List<RoomGroup> roomGroups = roomGroupDao.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomGroup::getRoomId, Function.identity()));
    }

}
