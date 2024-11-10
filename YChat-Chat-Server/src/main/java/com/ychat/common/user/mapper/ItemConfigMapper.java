package com.ychat.common.user.mapper;

import com.ychat.common.user.domain.entity.ItemConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 Mapper 接口
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
public interface ItemConfigMapper extends BaseMapper<ItemConfig> {

    List<ItemConfig> getByType(Integer type);
}
