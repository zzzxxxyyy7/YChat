package com.ychat.common.User.Services.Impl;

import com.ychat.common.Chat.Enum.GroupRoleEnum;
import com.ychat.common.Chat.Services.adapter.ChatAdapter;
import com.ychat.common.Constants.Enums.Impl.RoomTypeEnum;
import com.ychat.common.User.Dao.GroupMemberDao;
import com.ychat.common.User.Dao.RoomDao;
import com.ychat.common.User.Dao.RoomFriendDao;
import com.ychat.common.User.Dao.RoomGroupDao;
import com.ychat.common.User.Domain.entity.*;
import com.ychat.common.User.Services.IRoomService;
import com.ychat.common.User.Services.cache.UserInfoCache;
import com.ychat.common.Utils.Assert.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class IRoomServiceImpl implements IRoomService {

    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private RoomGroupDao roomGroupDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private RoomDao roomDao;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomGroup createGroupRoom(Long uid) {
        // 获取已经存在的自身为群主的群聊记录
        List<GroupMember> selfGroup = groupMemberDao.getSelfGroup(uid);
        if (!CollectionUtils.isEmpty(selfGroup) && selfGroup.size() >= 3) {
            AssertUtil.isEmpty(selfGroup, "每个人最多只能创建三个群聊");
        }
        User user = userInfoCache.get(uid);
        Room room = createRoom(RoomTypeEnum.GROUP);
        // 创建群
        RoomGroup roomGroup = ChatAdapter.buildGroupRoom(user, room.getId());
        roomGroupDao.save(roomGroup);
        // 插入群主身份
        GroupMember leader = GroupMember.builder()
                .role(GroupRoleEnum.LEADER.getType())
                .groupId(roomGroup.getId())
                .uid(uid)
                .build();
        groupMemberDao.save(leader);
        return roomGroup;
    }

    private Room createRoom(RoomTypeEnum typeEnum) {
        Room insert = ChatAdapter.buildRoom(typeEnum);
        roomDao.save(insert);
        return insert;
    }

}

