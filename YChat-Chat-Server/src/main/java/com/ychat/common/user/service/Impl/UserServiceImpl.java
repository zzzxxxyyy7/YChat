package com.ychat.common.user.service.Impl;

import Constants.Enums.ItemEnum;
import Constants.Enums.ItemTypeEnum;
import Constants.Exception.BusinessException;
import com.ychat.common.user.Event.UserRegisterEvent;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.dto.ModifyNameReq;
import com.ychat.common.user.domain.entity.ItemConfig;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.entity.UserBackpack;
import com.ychat.common.user.domain.vo.BadgeResp;
import com.ychat.common.user.domain.vo.UserInfoVo;
import com.ychat.common.user.service.IItemConfigService;
import com.ychat.common.user.service.IUserBackpackService;
import com.ychat.common.user.service.IUserService;
import com.ychat.common.user.service.adapter.UserAdapter;
import com.ychat.common.user.service.cache.ItemCache;
import Utils.Assert.AssertUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Rhss
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-11-05 06:04:49
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private IUserBackpackService userBackpackService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private ItemCache itemCache;

    @Autowired
    private IItemConfigService iItemConfigService;

    @Autowired
    private ApplicationEventPublisher appEventPublisher;

    @Override
    public User getById(Long uid) {
        return userDao.getById(uid);
    }

    @Override
    @Transactional
    public Long register(User newUser) {
        userDao.save(newUser);
        // 当前这个类(this) , 发布了这个事件
        appEventPublisher.publishEvent(new UserRegisterEvent(this, newUser));
        return newUser.getId();
    }

    @Override
    public UserInfoVo getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        int modifyNameChance = userBackpackService.getModifyNameChance(uid , ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoVo(user , modifyNameChance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyName(Long uid, ModifyNameReq req) {
        User oldUser = userDao.getByName(req.getName());
        AssertUtil.isEmpty(oldUser, "名字已经被占用");

        // 使用 Redisson 获取分布式锁
        RLock lock = redissonClient.getLock("modifyName:uid:" + uid);
        try {
            // 尝试加锁，设置锁的过期时间为5秒，防止长时间占用
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                // 判断改名卡是否足够
                UserBackpack firstValidItem = userBackpackService.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());

                AssertUtil.isNotEmpty(firstValidItem, "改名卡不足，请等待活动发放");

                // 使用改名卡
                boolean isUsed = userBackpackService.useItem(firstValidItem);
                if (!isUsed) {
                    userDao.modifyName(uid, req.getName());
                }
            } else {
                throw new BusinessException("操作过于频繁，请稍后再试");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("锁等待被中断");
        } finally {
            // 确保在操作结束后释放锁
            lock.unlock();
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        // 拿到徽章列表
        List<ItemConfig> badgeList = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户背包中所拥有的徽章
        List<UserBackpack> userBackpackList = userBackpackService.getByItemIds(uid , badgeList.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 拿到用户当前佩戴的徽章
        User user = userDao.getById(uid);
        Long itemId = user.getItemId();
        return UserAdapter.buildBadgeRespList(badgeList, userBackpackList, itemId);
    }

    @Override
    public void wearingBadge(Long uid, Long itemId) {
        // 确保有徽章
        UserBackpack userBackpack = userBackpackService.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(userBackpack, "未持有该徽章");
        // 确定这个是徽章
        ItemConfig itemConfig = iItemConfigService.getById(userBackpack.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "佩戴的物品不是徽章");
        userDao.wearingBadge(uid, itemId);
    }
}






