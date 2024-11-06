package com.ychat.common.user.service.Impl;

import com.ychat.common.user.service.LoginService;
import com.ychat.common.user.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void renewalTokenIfNecessary(String token) {

    }

    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        // TODO 存入 Redis

        return token;
    }

    @Override
    public Long getValidUid(String token) {
        return 0L;
    }
}
