package com.ychat.common.chat.service.cache;


import com.ychat.Utils.Cache.AbstractRedisStringCache;
import com.ychat.common.config.Redis.RedisKeyBuilder;
import com.ychat.common.user.dao.RoomDao;
import com.ychat.common.user.dao.RoomFriendDao;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 房间基本信息的缓存
 */
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private RoomFriendDao roomFriendDao;

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
