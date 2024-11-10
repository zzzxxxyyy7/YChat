package com.ychat.common.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ychat.common.user.domain.entity.ItemConfig;
import com.ychat.common.user.mapper.ItemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-09
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

    @Autowired
    private ItemConfigMapper itemConfigMapper;

    public List<ItemConfig> getByType(Integer type) {
        return itemConfigMapper.getByType(type);
    }
}
