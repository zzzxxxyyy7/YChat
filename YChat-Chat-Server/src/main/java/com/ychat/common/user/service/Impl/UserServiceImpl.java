package com.ychat.common.user.service.Impl;

import com.ychat.common.Enums.ItemEnum;
import com.ychat.common.Exception.BusinessException;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.dto.ModifyNameReq;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.entity.UserBackpack;
import com.ychat.common.user.domain.vo.BadgeResp;
import com.ychat.common.user.domain.vo.UserInfoVo;
import com.ychat.common.user.service.IUserBackpackService;
import com.ychat.common.user.service.IUserService;
import com.ychat.common.user.service.adapter.UserAdapter;
import com.ychat.common.utils.Assert.AssertUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Override
    @Transactional
    public Long register(User newUser) {
        userDao.save(newUser);
        // TODO 注册用户通知
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

                // TODO 改名卡发放
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
        return Collections.emptyList();
    }
}






