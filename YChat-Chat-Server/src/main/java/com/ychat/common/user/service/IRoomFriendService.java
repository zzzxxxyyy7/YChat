package com.ychat.common.user.service;

import com.ychat.common.user.domain.entity.RoomFriend;

import java.util.List;

/**
 * 单聊会话表 服务类
 */
public interface IRoomFriendService {

    /**
     * 创建一个单聊房间
     */
    RoomFriend createFriendRoom(List<Long> uidList);

}
