package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.Constants.Enums.Impl.ApplyStatusEnum;
import com.ychat.common.Constants.Enums.Impl.ApplyTypeEnum;
import com.ychat.common.User.Domain.entity.UserApply;
import com.ychat.common.User.Mapper.UserApplyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ychat.common.Constants.Enums.Impl.ApplyReadStatusEnum.READ;
import static com.ychat.common.Constants.Enums.Impl.ApplyReadStatusEnum.UNREAD;
import static com.ychat.common.Constants.Enums.Impl.ApplyStatusEnum.AGREE;

/**
 * 用户申请表 服务实现类
 */
@Service
public class UserApplyDao extends ServiceImpl<UserApplyMapper, UserApply> {

    /**
     * 拿到当前用户 uid 到目标用户 targetUid 的好友申请记录 --- 可能为 null
     * @param uid
     * @param targetUid
     * @return
     */
    public UserApply getFriendApproving(Long uid, Long targetUid) {
        return lambdaQuery().eq(UserApply::getUid, uid)
                .eq(UserApply::getTargetId, targetUid)
                .eq(UserApply::getStatus, ApplyStatusEnum.WAIT_APPROVAL)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getCode())
                .one();
    }

    /**
     * 获取好友申请记录中还是未读是数量个数
     * @param targetId
     * @return
     */
    public Integer getUnReadCount(Long targetId) {
        return lambdaQuery().eq(UserApply::getTargetId, targetId)
                .eq(UserApply::getReadStatus, UNREAD.getCode())
                .count();
    }

    public IPage<UserApply> friendApplyPage(Long uid, Page page) {
        return lambdaQuery()
                .eq(UserApply::getTargetId, uid)
                .eq(UserApply::getType, ApplyTypeEnum.ADD_FRIEND.getCode())
                .orderByDesc(UserApply::getCreateTime)
                .page(page);
    }

    public void readApples(Long uid, List<Long> applyIds) {
        lambdaUpdate()
                .set(UserApply::getReadStatus, READ.getCode())
                .eq(UserApply::getReadStatus, UNREAD.getCode())
                .in(UserApply::getId, applyIds)
                .eq(UserApply::getTargetId, uid)
                .update();
    }

    /**
     * 同意好友申请
     * @param applyId
     */
     public void agree(Long applyId) {
        lambdaUpdate().set(UserApply::getStatus, AGREE.getCode())
                .eq(UserApply::getId, applyId)
                .update();
    }

}
