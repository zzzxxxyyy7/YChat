package com.ychat.common.Chat.Services.adapter;

import com.ychat.common.Constants.Enums.Impl.HotFlagEnum;
import com.ychat.common.Constants.Enums.Impl.NormalOrNoEnum;
import com.ychat.common.Constants.Enums.Impl.RoomTypeEnum;
import com.ychat.common.User.Domain.entity.Room;
import com.ychat.common.User.Domain.entity.RoomFriend;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
     * 创建一个局部非热点会话
     * @param typeEnum
     * @return
     */
    public static Room buildRoom(RoomTypeEnum typeEnum) {
        Room room = new Room();
        room.setType(typeEnum.getType());
        room.setHotFlag(HotFlagEnum.NOT.getType());
        return room;
    }

    /**
     * 创建一个局部单聊会话
     * @param roomId
     * @param uidList
     * @return
     */
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

    /**
     * 既然是单聊会话，A 的会话列表要展示 B 的信息，B 的会话列表要展示 A 的信息
     * @param values
     * @param uid
     * @return
     */
    public static Set<Long> getFriendUidSet(Collection<RoomFriend> values, Long uid) {
        return values.stream()
                .map(a -> getFriendUid(a, uid))
                .collect(Collectors.toSet());
    }

    /**
     * 获取被展示（即对方）的 UID
     */
    public static Long getFriendUid(RoomFriend roomFriend, Long uid) {
        return Objects.equals(uid, roomFriend.getUid1()) ? roomFriend.getUid2() : roomFriend.getUid1();
    }

}
