package com.ychat.common.user.service.Impl;

import com.ychat.common.user.service.IUserBackpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserBackpackService implements IUserBackpackService {

    @Override
    public int getModifyNameChance(Long uid, Long itemId) {
        return 0;
    }
}
