package com.ychat.common.websocket;

import cn.hutool.json.JSONUtil;
import com.ychat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ychat.common.websocket.domain.vo.req.WSBaseReq;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义消息处理器
 */
@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 事件捕捉器
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // TODO 关闭用户的连接
                //userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 基于事件驱动的多路复用框架
     * @param ctx
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


    private void userOffLine(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asLongText();
        log.info("用户离线，channelId:{}", channelId);
        // 关闭 Channel 实现用户离线
        ctx.close();
    }
}
