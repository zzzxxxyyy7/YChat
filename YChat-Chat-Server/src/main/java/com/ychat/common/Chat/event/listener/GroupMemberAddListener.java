package com.ychat.common.Chat.Event.listener;

import com.ychat.common.Chat.Event.GroupMemberAddEvent;
import com.ychat.common.Chat.Services.ChatService;
import com.ychat.common.Chat.Services.adapter.MemberAdapter;
import com.ychat.common.Chat.Services.adapter.RoomAdapter;
import com.ychat.common.Chat.Services.cache.GroupMemberCache;
import com.ychat.common.Chat.domain.dto.ChatMessageReq;
import com.ychat.common.User.Dao.UserDao;
import com.ychat.common.User.Domain.entity.GroupMember;
import com.ychat.common.User.Domain.entity.RoomGroup;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Services.Impl.PushService;
import com.ychat.common.User.Services.cache.UserInfoCache;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSBaseResp;
import com.ychat.common.Websocket.Domain.Vo.Resp.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 群聊新增群成员监听器
 */
@Component
@Slf4j
public class GroupMemberAddListener {

    @Autowired
    private UserInfoCache userInfoCache;

    @Autowired
    private ChatService chatService;

    @Autowired
    private GroupMemberCache groupMemberCache;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendAddMsg(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        Long inviteUid = event.getInviteUid();
        User user = userInfoCache.get(inviteUid);
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        ChatMessageReq chatMessageReq = RoomAdapter.buildGroupAddMessage(roomGroup, user, userInfoCache.getBatch(uidList));
        chatService.sendMsg(chatMessageReq, User.UID_SYSTEM);
    }

    @Async
    @TransactionalEventListener(classes = GroupMemberAddEvent.class, fallbackExecution = true)
    public void sendChangePush(GroupMemberAddEvent event) {
        List<GroupMember> memberList = event.getMemberList();
        RoomGroup roomGroup = event.getRoomGroup();
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomGroup.getRoomId());
        List<Long> uidList = memberList.stream().map(GroupMember::getUid).collect(Collectors.toList());
        List<User> users = userDao.listByIds(uidList);
        users.forEach(user -> {
            WSBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberAddWS(roomGroup.getRoomId(), user);
            pushService.sendPushMsg(ws, memberUidList);
        });
        // 移除缓存
        groupMemberCache.evictMemberUidList(roomGroup.getRoomId());
    }

}
