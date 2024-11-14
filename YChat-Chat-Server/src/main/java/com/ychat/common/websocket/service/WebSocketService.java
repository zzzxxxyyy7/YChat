package com.ychat.common.websocket.service;

import com.ychat.common.websocket.domain.vo.resp.WSBaseResp;
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

    /**
     * 单机才可以这么做，集群需要加一层路由服务
     * @param msg
     */
    void sendMsgToAll(WSBaseResp<?> msg);
}
