package com.ychat.common.User.Services;

public interface IpService {

    /**
     * 异步更新用户ip详情
     *
     * @param uid
     */
    void refreshIpDetailAsync(Long uid);

}
