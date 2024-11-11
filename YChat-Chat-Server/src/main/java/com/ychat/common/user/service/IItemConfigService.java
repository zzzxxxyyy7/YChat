package com.ychat.common.user.service;

import com.ychat.common.user.domain.entity.ItemConfig;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
public interface IItemConfigService {

    List<ItemConfig> getByType(Integer type);

    ItemConfig getById(Long itemId);
}
