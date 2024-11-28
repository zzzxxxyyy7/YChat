package com.ychat.common.User.Services;

import com.ychat.common.User.Domain.dto.req.BlackReq;
import com.ychat.common.User.Domain.entity.User;

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
