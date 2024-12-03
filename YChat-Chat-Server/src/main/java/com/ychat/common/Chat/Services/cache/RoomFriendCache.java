package com.ychat.common.Chat.Services.cache;

import com.ychat.Utils.Cache.AbstractRedisStringCache;
import com.ychat.common.Config.Redis.RedisKeyBuilder;
import com.ychat.common.User.Dao.RoomFriendDao;
import com.ychat.common.User.Domain.entity.RoomFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 单聊会话基本信息的缓存
 */
@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Override
    protected String getKey(Long groupId) {
        return RedisKeyBuilder.getKey(RedisKeyBuilder.GROUP_FRIEND_STRING, groupId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomGroups = roomFriendDao.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }

}
