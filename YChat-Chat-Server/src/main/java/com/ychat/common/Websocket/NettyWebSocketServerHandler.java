package com.ychat.common.Websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ychat.common.Websocket.Domain.Dto.WSAuthorize;
import com.ychat.common.Websocket.Domain.Enums.WSReqTypeEnum;
import com.ychat.common.Websocket.Domain.Vo.Req.WSBaseReq;
import com.ychat.common.Websocket.Services.WebSocketService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    public void channelActive(ChannelHandlerContext ctx) {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
    }

    // 客户端离线
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.warn("客户端主动离线![{}]", ctx.channel().id());
        userOffLine(ctx.channel());
    }

    /**
     * Channel 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx.channel());
    }

    /**
     * 事件捕捉器
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 如果是空闲状态检测事件
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲事件触发用户下线 -- 客户端被动下线
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                userOffLine(ctx.channel());
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) { // 如果遇到了握手事件
            this.webSocketService.connect(ctx.channel());
            String token = NettyUtils.getAttr(ctx.channel(), NettyUtils.USER_TOKEN);
            /**
             * 如果这条 Channel 已经绑定了 token，则进行授权（即连接请求携带了 token 在 url 中）
             * 第一个登录的是不会携带 token 的，所以需要再判断
             */
            if (StringUtils.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), new WSAuthorize(token));
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 当通道发生异常时
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught: " , cause);
        super.exceptionCaught(ctx, cause);
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
                log.info("真听到来自 {} 的心跳信息", ctx.channel().remoteAddress());
                break;
            case AUTHORIZE:
                this.webSocketService.authorize(ctx.channel() , new WSAuthorize(wsBaseReq.getToken()));
                break;
            case LOGIN:
                this.webSocketService.handleLoginReq(ctx.channel());
                break;
        }
    }

    /**
     * 客户端断开，执行下线逻辑
     * @param ctx
     */
    private void userOffLine(Channel ctx) {
        this.webSocketService.removed(ctx);
        // 关闭 Channel 实现用户离线
        ctx.close();
    }

}
