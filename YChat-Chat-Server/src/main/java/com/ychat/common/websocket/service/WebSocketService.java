package com.ychat.common.websocket.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * Description: websocket处理类
 */
public interface WebSocketService {

    void saveChannel(ChannelHandlerContext ctx);

    void handleLoginReq(Channel channel) throws WxErrorException;

    void offline(ChannelHandlerContext ctx);
}
