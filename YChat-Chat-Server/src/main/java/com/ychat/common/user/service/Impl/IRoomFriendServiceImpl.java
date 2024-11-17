package com.ychat.common.user.service.Impl;

import Constants.Enums.NormalOrNoEnum;
import Constants.Enums.RoomTypeEnum;
import com.ychat.common.user.dao.RoomDao;
import com.ychat.common.user.dao.RoomFriendDao;
import com.ychat.common.user.domain.entity.Room;
import com.ychat.common.user.domain.entity.RoomFriend;
import com.ychat.common.user.service.IRoomFriendService;
import com.ychat.common.user.service.adapter.ChatAdapter;
import Utils.Assert.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class IRoomFriendServiceImpl implements IRoomFriendService {

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);

        RoomFriend roomFriend = roomFriendDao.getByKey(key);
        if (Objects.nonNull(roomFriend)) { // 如果存在会话记录就恢复，适用于重新加回好友
            restoreRoomIfNeed(roomFriend);
        } else { // 新建房间
            Room room = createRoom(RoomTypeEnum.FRIEND);
            roomFriend = doCreateFriendRoom(room.getId(), uidList);
        }

        return roomFriend;
    }

    private RoomFriend doCreateFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend newRoomFriend = ChatAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(newRoomFriend);
        return newRoomFriend;
    }

    private Room createRoom(RoomTypeEnum typeEnum) {
        Room insert = ChatAdapter.buildRoom(typeEnum);
        roomDao.save(insert);
        return insert;
    }

    /**
     * 重启历史会话
     * @param room
     */
    private void restoreRoomIfNeed(RoomFriend room) {
        if (Objects.equals(room.getStatus(), NormalOrNoEnum.NOT_NORMAL.getStatus())) {
            roomFriendDao.restoreRoom(room.getId());
        }
    }

}
