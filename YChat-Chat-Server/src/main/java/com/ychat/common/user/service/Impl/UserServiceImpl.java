package com.ychat.common.user.service.Impl;

import com.ychat.common.Enums.ItemEnum;
import com.ychat.common.user.dao.UserDao;
import com.ychat.common.user.domain.dto.ModifyNameReq;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.entity.UserBackpack;
import com.ychat.common.user.domain.vo.UserInfoVo;
import com.ychat.common.user.service.IUserBackpackService;
import com.ychat.common.user.service.IUserService;
import com.ychat.common.user.service.adapter.UserAdapter;
import com.ychat.common.utils.Assert.AssertUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void modifyName(Long uid, ModifyNameReq req) {
        //判断名字是不是重复
        String newName = req.getName();
        AssertUtil.isFalse(StringUtils.isEmpty(newName), "名字不能为空");
        AssertUtil.isFalse(newName.length() > 6, "名字不能超过6个字哦");
        //AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(newName), "名字中包含敏感词，请重新输入"); // 判断名字中有没有敏感词
        User oldUser = userDao.getByName(newName);
        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个哦~~");
        //判断改名卡够不够
        UserBackpack firstValidItem = userBackpackService.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem, "改名次数不够了，等后续活动送改名卡哦");

    }

}






