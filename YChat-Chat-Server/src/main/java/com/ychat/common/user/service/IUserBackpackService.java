package com.ychat.common.user.service;

import com.ychat.common.Constants.Enums.Impl.IdempotentEnum;
import com.ychat.common.user.domain.entity.UserBackpack;

import java.util.List;

/**
 * 用户背包表 服务类
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
public interface IUserBackpackService {

    int getModifyNameChance(Long uid, Long itemId);

    UserBackpack getFirstValidItem(Long uid, Long itemId);

    boolean useItem(UserBackpack firstValidItem);

    List<UserBackpack> getByItemIds(Long uid, List<Long> ItemIds);

    /**
     * 获取物品 - 幂等接口
     * @param uid 用户 ID
     * @param itemId 物品 ID
     */
    void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId);
}
