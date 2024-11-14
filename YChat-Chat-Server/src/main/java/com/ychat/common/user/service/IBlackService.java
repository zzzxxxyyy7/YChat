package com.ychat.common.user.service;

import com.ychat.common.user.domain.dto.BlackReq;
import com.ychat.common.user.domain.entity.User;

/**
 * 黑名单服务类
 *
 * @author <a href="https://github.com/zongzibinbin">Rhss</a>
 * @since 2024-11-14
 */
public interface IBlackService {

    void blackUid(BlackReq req);

    void BlackIp(String ip);

    void BlackIp(User user);
}
