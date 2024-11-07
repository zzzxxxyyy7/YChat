package com.ychat.common.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ychat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ychat.common.websocket.domain.vo.req.WSBaseReq;
import com.ychat.common.websocket.service.WebSocketService;
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

    private WebSocketService webSocketService;

    /**
     * 连接活跃时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.saveChannel(ctx);
    }

    /**
     * 客户端主动下线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

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
            // 读空闲事件触发用户下线
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // TODO 关闭用户的连接
                userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手完成");
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 基于事件驱动的多路复用框架
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsBaseReq.getType())) {
            case HEARTBEAT:
                break;
            case AUTHORIZE:
                this.webSocketService.authorize(ctx.channel() , wsBaseReq.getToken());
                break;
            case LOGIN:
                this.webSocketService.handleLoginReq(ctx.channel());
                log.info("请求二维码 = {}", msg.text());
                break;
        }
    }


    private void userOffLine(ChannelHandlerContext ctx) {
        String channelId = ctx.channel().id().asLongText();
        log.info("用户离线，channelId:{}", channelId);
        webSocketService.offline(ctx);
        // 关闭 Channel 实现用户离线
        ctx.close();
    }
}
