package com.ychat.common.user.service;

public interface LoginService {

    /**
     * 刷新token有效期
     *
     * @param token
     */
    void renewalTokenIfNecessary(String token);

    /**
     * 登录成功，获取token
     *
     * @param uid
     * @return 返回token
     */
    String login(Long uid);

    /**
     * 如果token有效，返回uid（可用于校验 Token 是否有效）
     *
     * @param token
     * @return
     */
    Long getValidUid(String token);

}