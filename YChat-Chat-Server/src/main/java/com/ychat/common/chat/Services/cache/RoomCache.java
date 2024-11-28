package com.ychat.common.Chat.Services.cache;


import com.ychat.Utils.Cache.AbstractRedisStringCache;
import com.ychat.common.Config.Redis.RedisKeyBuilder;
import com.ychat.common.User.Dao.RoomDao;
import com.ychat.common.User.Domain.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 会话基本信息的缓存
 */
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {

    @Autowired
    private RoomDao roomDao;

    @Override
    protected String getKey(Long roomId) {
        return RedisKeyBuilder.getKey(RedisKeyBuilder.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomDao.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }

}
