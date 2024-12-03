package com.ychat.common.User.Services.Impl;

import com.ychat.common.Chat.Services.adapter.ChatAdapter;
import com.ychat.common.User.Dao.RoomFriendDao;
import com.ychat.common.User.Domain.entity.RoomFriend;
import com.ychat.common.User.Services.IRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class IRoomServiceImpl implements IRoomService {

    @Autowired
    private RoomFriendDao roomFriendDao;

    /**
     * 查询单聊会话
     * @param uid1
     * @param uid2
     * @return
     */
    @Override
    public RoomFriend getFriendRoom(Long uid1, Long uid2) {
        String key = ChatAdapter.generateRoomKey(Arrays.asList(uid1, uid2));
        return roomFriendDao.getByKey(key);
    }

}

