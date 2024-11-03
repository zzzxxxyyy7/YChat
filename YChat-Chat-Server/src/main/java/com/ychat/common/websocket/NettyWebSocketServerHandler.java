package com.ychat.common.websocket;

import cn.hutool.json.JSONUtil;
import com.ychat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ychat.common.websocket.domain.vo.req.WSBaseReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 自定义消息处理器
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 基于事件驱动的多路复用框架
     * @param channelHandlerContext
     * @param textWebSocketFrame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(textWebSocketFrame.text(), WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                break;
            case LOGIN:
                System.out.println("请求登录二维码");
                ctx.channel().writeAndFlush(new TextWebSocketFrame("12312"));
        }
    }

}
