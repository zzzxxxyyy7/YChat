package com.ychat.common.User.Services.Impl;

import com.ychat.common.User.Dao.ItemConfigDao;
import com.ychat.common.User.Domain.entity.ItemConfig;
import com.ychat.common.User.Services.IItemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ItemConfigServiceImpl implements IItemConfigService {

    @Autowired
    private ItemConfigDao itemConfigDao;

    @Override
    public List<ItemConfig> getByType(Integer type) {
        return itemConfigDao.getByType(type);
    }

    @Override
    public ItemConfig getById(Long itemId) {
        return itemConfigDao.getById(itemId);
    }
}
