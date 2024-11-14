package com.ychat.common.user.service.Impl;

import com.ychat.common.Enums.BlackTypeEnum;
import com.ychat.common.user.Event.UserBlackEvent;
import com.ychat.common.user.dao.BlackDao;
import com.ychat.common.user.domain.dto.BlackReq;
import com.ychat.common.user.domain.entity.Black;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IBlackService;
import com.ychat.common.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BlackServiceImpl implements IBlackService {

    @Autowired
    private BlackDao blackDao;

    @Autowired
    private IUserService usersService;

    @Autowired
    private ApplicationEventPublisher appEventPublisher;

    /**
     * 拉黑用户
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void blackUid(BlackReq req) {
        Long uid = req.getUid();
        Black black = new Black();
        black.setTarget(uid.toString());
        black.setType(BlackTypeEnum.UID.getType());
        blackDao.save(black);
        User user = usersService.getById(uid);
        BlackIp(user.getIpInfo().getCreateIp());
        BlackIp(user.getIpInfo().getUpdateIp());
        appEventPublisher.publishEvent(new UserBlackEvent(this, user));
    }

    /**
     * 拉黑 IP
     * @param ip
     */
    public void BlackIp(String ip) {
        if (ip == null) return ;
        Black black = new Black();
        black.setTarget(ip);
        black.setType(BlackTypeEnum.IP.getType());
        blackDao.save(black);
    }
}
