package com.ychat.common.User.Services;

import com.ychat.common.User.Domain.entity.ItemConfig;

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
