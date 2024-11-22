package com.ychat.common.user.dao;

import com.ychat.common.user.domain.entity.GroupMember;
import com.ychat.common.user.mapper.GroupMemberMapper;
import com.ychat.common.user.service.IGroupMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会话成员表 服务实现类
 *
 * @author ${author}
 * @since 2024-11-23
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
    }
}
