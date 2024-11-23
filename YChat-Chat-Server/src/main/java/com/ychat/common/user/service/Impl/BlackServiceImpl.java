package com.ychat.common.user.service.Impl;

import Constants.Enums.Impl.BlackTypeEnum;
import Constants.Exception.BusinessException;
import com.ychat.common.user.Event.UserBlackEvent;
import com.ychat.common.user.dao.BlackDao;
import com.ychat.common.user.domain.dto.req.BlackReq;
import com.ychat.common.user.domain.entity.Black;
import com.ychat.common.user.domain.entity.IpInfo;
import com.ychat.common.user.domain.entity.User;
import com.ychat.common.user.service.IBlackService;
import com.ychat.common.user.service.IUserService;
import Utils.Assert.AssertUtil;
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
        User user = usersService.getById(uid);
        AssertUtil.isNotEmpty(user, "拉黑的用户不存在");
        Black black = new Black();
        black.setTarget(uid.toString());
        black.setType(BlackTypeEnum.UID.getType());
        try {
            blackDao.save(black);
        } catch (Exception e) {
            log.error("拉黑用户失败，userId:{}, reason is {}", uid, e.getMessage());
            throw new BusinessException("拉黑用户失败");
        }
        BlackIp(user);
        appEventPublisher.publishEvent(new UserBlackEvent(this, user));
    }

    /**
     * 拉黑 IP
     * @param user
     */
    public void BlackIp(User user) {
        IpInfo ipInfo = user.getIpInfo();
        if (ipInfo == null) return;
        String createIp = ipInfo.getCreateIp();
        String updateIp = ipInfo.getCreateIp();
        judgeIp(createIp);
        judgeIp(updateIp);
    }

    @Override
    public void BlackIp(String ip) {
        judgeIp(ip);
    }

    void judgeIp(String ip) {
        if (ip == null) return ;
        Black black = new Black();
        black.setTarget(ip);
        black.setType(BlackTypeEnum.IP.getType());
        blackDao.save(black);
    }

}
