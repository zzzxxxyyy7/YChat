package com.ychat.common.User.Services;

import com.ychat.common.User.Domain.entity.RoomFriend;

/**
 * 房间表 服务类
 */
public interface IRoomService {

    /**
     * 查询单聊会话
     * @param uid
     * @param friendUid
     * @return
     */
    RoomFriend getFriendRoom(Long uid, Long friendUid);

}
