package com.ychat.common.user.service.Impl;

import com.ychat.common.user.dao.UserBackpackDao;
import com.ychat.common.user.domain.entity.UserBackpack;
import com.ychat.common.user.service.IUserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class UserBackpackService implements IUserBackpackService {

    @Autowired
    private UserBackpackDao userBackpackDao;

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
}
