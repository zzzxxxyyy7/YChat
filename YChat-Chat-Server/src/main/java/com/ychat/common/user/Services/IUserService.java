package com.ychat.common.User.Services;

import com.ychat.common.User.Domain.dto.SummeryInfoDTO;
import com.ychat.common.User.Domain.dto.req.ItemInfoReq;
import com.ychat.common.User.Domain.dto.req.ModifyNameReq;
import com.ychat.common.User.Domain.dto.req.SummeryInfoReq;
import com.ychat.common.User.Domain.entity.User;
import com.ychat.common.User.Domain.vo.BadgeResp;
import com.ychat.common.User.Domain.vo.ItemInfoVo;
import com.ychat.common.User.Domain.vo.UserInfoVo;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户表 服务类
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-04
 */
public interface IUserService {

    User getById(Long uid);

    /**
     * 用户注册，需要获得id
     * @param user
     */
    Long register(User user);

    /**
     * 获取用户信息
     * @param uid
     * @return
     */
    UserInfoVo getUserInfo(Long uid);

    /**
     * 修改用户名
     *
     * @param uid
     * @param req
     */
    void modifyName(Long uid, ModifyNameReq req);

    /**
     * 获取徽章列表
     * @param uid
     * @return
     */
    List<BadgeResp> badges(Long uid);

    /**
     * 佩戴徽章
     * @param uid
     * @param itemId
     */
    void wearingBadge(Long uid, Long itemId);

    /**
     * 懒加载获取用户汇总信息
     *
     * @param req
     * @return
     */
    List<SummeryInfoDTO> getSummeryUserInfo(@Valid SummeryInfoReq req);

    /**
     * 获取徽章汇总信息
     *
     * @param req
     * @return
     */
    List<ItemInfoVo> getItemInfo(@Valid ItemInfoReq req);

}
