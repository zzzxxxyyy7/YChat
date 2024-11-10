package com.ychat.common.user.service.cache;

import com.ychat.common.user.domain.entity.ItemConfig;
import com.ychat.common.user.service.IItemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ItemCache {

    @Autowired
    private IItemConfigService itemService;

    public List<ItemConfig> getByType(Integer type) {
        return itemService.getByType(type);
    }


}
