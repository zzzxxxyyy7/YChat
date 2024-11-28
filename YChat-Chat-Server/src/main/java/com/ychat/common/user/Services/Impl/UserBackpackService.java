package com.ychat.common.User.Services.Impl;

import com.ychat.common.Constants.Enums.Impl.IdempotentEnum;
import com.ychat.common.Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.common.Constants.Exception.BusinessException;
import com.ychat.common.User.Dao.UserBackpackDao;
import com.ychat.common.User.Domain.entity.UserBackpack;
import com.ychat.common.User.Services.IUserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class UserBackpackService implements IUserBackpackService {

    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public int getModifyNameChance(Long uid, Long itemId) {
        return userBackpackDao.getModifyNameChance(uid, itemId);
    }

    @Override
    public UserBackpack getFirstValidItem(Long uid, Long itemId) {
        return userBackpackDao.getFirstValidItem(uid,  itemId);
    }

    @Override
    public boolean useItem(UserBackpack firstValidItem) {
        return userBackpackDao.useItem(firstValidItem);
    }

    @Override
    public List<UserBackpack> getByItemIds(Long uid, List<Long> ItemIds) {
        return userBackpackDao.getByItemIds(uid, ItemIds);
    }

    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        RLock lock = redissonClient.getLock(idempotent);
        try  {
            if (lock.tryLock()) {
                UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
                if (Objects.nonNull(userBackpack)) {
                    log.info("用户{}已经拥有该物品，无需重复获取", uid);
                    return;
                }
                // TODO 业务检查
                UserBackpack newUserBackpack = UserBackpack.builder()
                        .uid(uid)
                        .itemId(itemId)
                        .status(YesOrNoEnum.NO.getStatus())
                        .idempotent(idempotent)
                        .build();
                userBackpackDao.save(newUserBackpack);
            } else {
                throw new BusinessException("操作过于频繁，请稍后再试");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 构造幂等键
     * @param itemId 物品ID
     * @param idempotentEnum 业务场景枚举
     * @param businessId 业务ID
     */
    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
