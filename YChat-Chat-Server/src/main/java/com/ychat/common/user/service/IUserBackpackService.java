package com.ychat.common.user.service;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
public interface IUserBackpackService {

    int getModifyNameChance(Long uid, Long itemId);
}
