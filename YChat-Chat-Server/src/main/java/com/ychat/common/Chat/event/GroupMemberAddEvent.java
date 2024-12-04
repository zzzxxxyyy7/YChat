package com.ychat.common.Chat.Event;

import com.ychat.common.User.Domain.entity.GroupMember;
import com.ychat.common.User.Domain.entity.RoomGroup;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * 群聊新增群成员事件
 */
@Getter
public class GroupMemberAddEvent extends ApplicationEvent {

    private final List<GroupMember> memberList;
    private final RoomGroup roomGroup;
    private final Long inviteUid;

    public GroupMemberAddEvent(Object source, RoomGroup roomGroup, List<GroupMember> memberList, Long inviteUid) {
        super(source);
        this.memberList = memberList;
        this.roomGroup = roomGroup;
        this.inviteUid = inviteUid;
    }

}
