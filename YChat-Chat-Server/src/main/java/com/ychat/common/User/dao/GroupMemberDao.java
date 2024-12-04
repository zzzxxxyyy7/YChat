package com.ychat.common.User.Dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ychat.common.Chat.Enum.GroupRoleEnum;
import com.ychat.common.Chat.Services.cache.GroupMemberCache;
import com.ychat.common.User.Domain.entity.GroupMember;
import com.ychat.common.User.Mapper.GroupMemberMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ychat.common.Chat.Enum.GroupRoleEnum.ADMIN_LIST;

/**
 * 会话成员表 服务实现类
 */
@Service
public class GroupMemberDao extends ServiceImpl<GroupMemberMapper, GroupMember> {

    @Autowired
    private GroupMemberCache groupMemberCache;

    public GroupMember getMember(Long groupId, Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .eq(GroupMember::getUid, uid)
                .one();
    }

    public List<Long> getMemberUidList(Long groupId) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     * 批量获取成员在群聊会话中的角色
     *
     * @param groupId 群ID
     * @param uidList 用户列表
     * @return 成员群角色列表
     */
    public Map<Long, Integer> getMemberMapRole(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .in(GroupMember::getRole, ADMIN_LIST)
                .select(GroupMember::getUid, GroupMember::getRole)
                .list();
        return list.stream().collect(Collectors.toMap(GroupMember::getUid, GroupMember::getRole));
    }

    /**
     * 是否是群主
     *
     * @param id  群组ID
     * @param uid 用户ID
     * @return 是否是群主
     */
    public Boolean isLord(Long id, Long uid) {
        GroupMember groupMember = this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 是否是管理员
     *
     * @param id  群组ID
     * @param uid 用户ID
     * @return 是否是管理员
     */
    public Boolean isManager(Long id, Long uid) {
        GroupMember groupMember = this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.MANAGER.getType())
                .one();
        return ObjectUtil.isNotNull(groupMember);
    }

    /**
     * 判断用户是否在房间中
     *
     * @param roomId  房间ID
     * @param uidList 用户ID
     * @return 是否在群聊中
     */
    public Boolean isGroupShip(Long roomId, List<Long> uidList) {
        List<Long> memberUidList = groupMemberCache.getMemberUidList(roomId);
        // TODO 直接调用 List.contains 可能具有较差性能，改用 HashSet.contains 性能或许更高？
        return new HashSet<>(memberUidList).containsAll(uidList);
    }

    /**
     * 根据群组ID删除群成员
     *
     * @param groupId 群组ID
     * @param uidList 群成员列表
     * @return 是否删除成功
     */
    public Boolean removeByGroupId(Long groupId, List<Long> uidList) {
        if (CollectionUtil.isNotEmpty(uidList)) {
            LambdaQueryWrapper<GroupMember> wrapper = new QueryWrapper<GroupMember>()
                    .lambda()
                    .eq(GroupMember::getGroupId, groupId)
                    .in(GroupMember::getUid, uidList);
            return this.remove(wrapper);
        }
        return false;
    }

    public List<GroupMember> getSelfGroup(Long uid) {
        return lambdaQuery()
                .eq(GroupMember::getUid, uid)
                .eq(GroupMember::getRole, GroupRoleEnum.LEADER.getType())
                .list();
    }

    public List<Long> getMemberBatch(Long groupId, List<Long> uidList) {
        List<GroupMember> list = lambdaQuery()
                .eq(GroupMember::getGroupId, groupId)
                .in(GroupMember::getUid, uidList)
                .select(GroupMember::getUid)
                .list();
        return list.stream().map(GroupMember::getUid).collect(Collectors.toList());
    }

    /**
     * 获取管理员uid列表
     *
     * @param id 群主ID
     * @return 管理员uid列表
     */
    public List<Long> getManageUidList(Long id) {
        return this.lambdaQuery()
                .eq(GroupMember::getGroupId, id)
                .eq(GroupMember::getRole, GroupRoleEnum.MANAGER.getType())
                .list()
                .stream()
                .map(GroupMember::getUid)
                .collect(Collectors.toList());
    }

    /**
     * 增加管理员
     *
     * @param id      群组ID
     * @param uidList 用户列表
     */
    public void addAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.MANAGER.getType());
        this.update(wrapper);
    }

    /**
     * 撤销管理员
     *
     * @param id      群组ID
     * @param uidList 用户列表
     */
    public void revokeAdmin(Long id, List<Long> uidList) {
        LambdaUpdateWrapper<GroupMember> wrapper = new UpdateWrapper<GroupMember>().lambda()
                .eq(GroupMember::getGroupId, id)
                .in(GroupMember::getUid, uidList)
                .set(GroupMember::getRole, GroupRoleEnum.MEMBER.getType());
        this.update(wrapper);
    }

}
