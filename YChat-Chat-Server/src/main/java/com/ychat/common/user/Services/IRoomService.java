package com.ychat.common.User.Services;

import com.ychat.common.User.Domain.entity.RoomFriend;
import com.ychat.common.User.Domain.entity.RoomGroup;

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

    /**
     * 创建一个群聊房间
     */
    RoomGroup createGroupRoom(Long uid);

}
