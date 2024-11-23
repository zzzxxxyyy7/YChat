package com.ychat.common.user.service.adapter;

import cn.hutool.core.util.RandomUtil;
import com.ychat.common.Constants.Enums.Impl.YesOrNoEnum;
import com.ychat.common.user.domain.entity.ItemConfig;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.domain.entity.UserBackpack;
import com.ychat.common.user.domain.vo.BadgeResp;
import com.ychat.common.user.domain.vo.UserInfoVo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 用户适配器
 */
@Slf4j
public class UserAdapter {

    public static User buildUser(String openId) {
        User user = new User();
        user.setOpenId(openId);
        return user;
    }

    public static User buildAuthorizeUser(Long id, WxOAuth2UserInfo userInfo) {
        User user = new User();
        user.setId(id);
        user.setAvatar(userInfo.getHeadImgUrl());
        user.setName(userInfo.getNickname());
        user.setSex(userInfo.getSex());
        if (userInfo.getNickname().length() > 6) {
            user.setName("名字过长" + RandomUtil.randomInt(100000));
        } else {
            user.setName(userInfo.getNickname());
        }
        return user;
    }

    public static UserInfoVo buildUserInfoVo(User user, int modifyNameChance) {
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setId(user.getId());
        userInfoVo.setName(user.getName());
        userInfoVo.setAvatar(user.getAvatar());
        userInfoVo.setModifyNameChance(modifyNameChance);
        return userInfoVo;
    }

    public static List<BadgeResp> buildBadgeRespList(List<ItemConfig> badgeList, List<UserBackpack> userBackpackList, Long itemId) {
        // 用户已经拥有的 ID
        Set<Long> obtainIds = userBackpackList.stream().map(UserBackpack::getItemId).collect(Collectors.toSet());

        return badgeList.stream().map(badge -> {
            BadgeResp badgeResp = new BadgeResp();
            badgeResp.setId(badge.getId());
            badgeResp.setImg(badge.getImg());
            badgeResp.setDescribe(badge.getDescribe());
            badgeResp.setObtain(obtainIds.contains(badge.getId()) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            badgeResp.setWearing(badge.getId().equals(itemId) ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus());
            return badgeResp;
        })
                .sorted(Comparator.comparing(BadgeResp::getWearing , Comparator.reverseOrder())
                .thenComparing(BadgeResp::getObtain , Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
}
