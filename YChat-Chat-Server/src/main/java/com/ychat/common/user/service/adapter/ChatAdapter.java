package com.ychat.common.user.service.adapter;

import Constants.Enums.HotFlagEnum;
import Constants.Enums.NormalOrNoEnum;
import Constants.Enums.RoomTypeEnum;
import com.ychat.common.user.domain.entity.Room;
import com.ychat.common.user.domain.entity.RoomFriend;

import java.util.List;
import java.util.stream.Collectors;

public class ChatAdapter {

    public static final String SEPARATOR = ",";

    /**
     * 根据uid列表生成一个单聊会话roomKey，默认 uid1,uid2
     * @param uidList
     * @return
     */
    public static String generateRoomKey(List<Long> uidList) {
        return uidList.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    /**
     * 创建一个非热点会话
     * @param typeEnum
     * @return
     */
    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }

    public static RoomFriend buildFriendRoom(Long roomId, List<Long> uidList) {
        List<Long> collect = uidList.stream().sorted().collect(Collectors.toList());
        RoomFriend roomFriend = new RoomFriend();
        roomFriend.setRoomId(roomId);
        roomFriend.setUid1(collect.get(0));
        roomFriend.setUid2(collect.get(1));
        roomFriend.setRoomKey(generateRoomKey(uidList));
        roomFriend.setStatus(NormalOrNoEnum.NORMAL.getStatus());
        return roomFriend;
    }

}
