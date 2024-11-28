package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> {

    public User getByOpenId(String openId) {
        LambdaQueryWrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getOpenId, openId);
        return getOne(wrapper);
    }

    public User getByName(String Name) {
        return lambdaQuery().eq(User::getName, Name).one();
    }

    /**
     * 修改用户名
     * @param uid
     * @param name
     */
    public void modifyName(Long uid, String name) {
        User update = new User();
        update.setId(uid);
        update.setName(name);
        updateById(update);
    }

    /**
     * 佩戴徽章 TODO 代校验 and 考虑并发
     * @param uid
     * @param itemId
     */
    public void wearingBadge(Long uid, Long itemId) {
        lambdaUpdate()
                .eq(User::getId, uid)
                .set(User::getItemId, itemId)
                .update();
    }

    /**
     * 更新账号状态，即禁用账号
     * @param id
     */
    public void invalidUid(Long id) {
        lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getStatus, YesOrNoEnum.YES.getStatus())
                .update();
    }

    /**
     * 获取好友列表
     * @param uids
     * @return
     */
    public List<User> getFriendList(List<Long> uids) {
        return lambdaQuery()
                .in(User::getId, uids)
                .select(User::getId, User::getActiveStatus, User::getName, User::getAvatar)
                .list();
    }
}
