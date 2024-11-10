package com.ychat.common.user.service.Impl;

import com.ychat.common.user.dao.ItemConfigDao;
import com.ychat.common.user.domain.entity.ItemConfig;
import com.ychat.common.user.service.IItemConfigService;
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
}
