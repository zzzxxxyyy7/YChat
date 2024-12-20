package com.ychat.common.User.Dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.User.Domain.entity.ItemConfig;
import com.ychat.common.User.Mapper.ItemConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 功能物品配置表 服务实现类
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    public List<ItemConfig> getByType(Integer type) {
        return lambdaQuery().eq(ItemConfig::getType, type).list();
    }
}
