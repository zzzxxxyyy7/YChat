package com.ychat.common.websocket.service;

import com.ychat.common.websocket.domain.dto.WSAuthorize;
import com.ychat.common.websocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * Description: websocket处理类
 */
public interface WebSocketService {

    /**
     * 处理用户登录请求，需要返回一张带code的二维码
     *
     * @param channel
     */
    void handleLoginReq(Channel channel) throws WxErrorException;

    /**
     * 处理所有ws连接的事件
     *
     * @param channel
     */
    void connect(Channel channel);

    /**
     * 处理ws断开连接的事件
     *
     * @param channel
     */
    void removed(Channel channel);

    /**
     * 主动认证登录
     *
     * @param channel
     * @param wsAuthorize
     */
    void authorize(Channel channel, WSAuthorize wsAuthorize);

    void scanLoginSuccess(Integer loginCode, Long uid);

    void waitAuthorize(Integer loginCode);

    /**
     * 单机才可以这么做，集群需要加一层路由服务
     * @param msg
     */
    void sendMsgToAll(WSBaseResp<?> msg);

    /**
     * 推送消息给单个在线用户
     * @param wsBaseResp
     * @param uid
     */
    void sendToUid(WSBaseResp<?> wsBaseResp, Long uid);

    /**
     * 推动消息给所有在线的人
     *
     * @param wsBaseResp 发送的消息体
     * @param skipUid    需要跳过的人
     */
    void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid);

}
