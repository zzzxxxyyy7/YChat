package com.ychat.common.websocket.service;

import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * Description: websocket处理类
 */
public interface WebSocketService {

    void saveChannel(Channel ctx);

    void handleLoginReq(Channel channel) throws WxErrorException;

    void offline(Channel ctx);

    void scanLoginSuccess(Integer loginCode, Long uid);

    void waitAuthorize(Integer loginCode);

    void authorize(Channel channel, String token);
}
