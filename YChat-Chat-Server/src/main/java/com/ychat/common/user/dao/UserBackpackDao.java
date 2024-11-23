package com.ychat.common.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.common.user.domain.entity.UserBackpack;
import com.ychat.common.user.mapper.UserBackpackMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {

    public int getModifyNameChance(Long uid, Long itemId) {
        return lambdaQuery()
            .eq(UserBackpack::getUid, uid)
            .eq(UserBackpack::getItemId, itemId)
            .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus()) // 获取未使用过的改名卡
            .count();
    }

    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        LambdaQueryWrapper<UserBackpack> wrapper = new QueryWrapper<UserBackpack>().lambda()
                .eq(UserBackpack::getUid, uid)
                .eq(UserBackpack::getItemId, itemId)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .last("limit 1");
        return getOne(wrapper);
    }

    /**
     * 使用改名卡
     * @param firstValidItem
     */
    public boolean useItem(UserBackpack firstValidItem) {
        UserBackpack update = new UserBackpack();
        update.setId(firstValidItem.getId());
        update.setStatus(YesOrNoEnum.YES.getStatus());
        return updateById(update);
    }

    public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
        return lambdaQuery().eq(UserBackpack::getUid, uid)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .list();
    }

    public List<UserBackpack> getByItemIds(List<Long> uids, List<Long> itemIds) {
        return lambdaQuery().in(UserBackpack::getUid, uids)
                .in(UserBackpack::getItemId, itemIds)
                .eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
                .list();
    }

    public UserBackpack getByIdempotent(String idempotent) {
        return lambdaQuery()
                .eq(UserBackpack::getIdempotent, idempotent)
                .one();

    }
}
