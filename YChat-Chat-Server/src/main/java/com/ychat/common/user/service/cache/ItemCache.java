package com.ychat.common.user.service.cache;

import com.ychat.common.user.domain.entity.ItemConfig;
import com.ychat.common.user.service.IItemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ItemCache {

    @Autowired
    private IItemConfigService itemService;

    /**
     * Cacheable 从 Spring cache 获取缓存，如果没有，就从数据库获取
     * CachePut
     * CacheEvict
     */

    @Cacheable(cacheNames = "item", key = "'itemsByType:'+#type")
    public List<ItemConfig> getByType(Integer type) {
        return itemService.getByType(type);
    }

}
