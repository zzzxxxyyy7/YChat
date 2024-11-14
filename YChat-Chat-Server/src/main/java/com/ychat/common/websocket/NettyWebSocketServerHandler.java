package com.ychat.common.websocket;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.ychat.common.websocket.domain.enums.WSReqTypeEnum;
import com.ychat.common.websocket.domain.vo.req.WSBaseReq;
import com.ychat.common.websocket.service.WebSocketService;
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        // 保存 channel 的游客态
        webSocketService.saveChannel(ctx.channel());
    }

    /**
     * 客户端主动下线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲事件触发用户下线 -- 客户端被动下线
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                userOffLine(ctx.channel());
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            String token = NettyUtils.getAttr(ctx.channel(), NettyUtils.USER_TOKEN);
            /**
             * 如果这条 Channel 已经绑定了 token，则进行授权（即连接请求携带了 token 在 url 中）
             * 第一个登录的是不会携带 token 的，所以需要再判断
             */
            if (StringUtils.isNotBlank(token)) {
                webSocketService.authorize(ctx.channel(), token);
            }
            log.info("握手成功");
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
                break;
            case AUTHORIZE:
                this.webSocketService.authorize(ctx.channel() , wsBaseReq.getToken());
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
        String channelId = ctx.id().asLongText();
        log.info("用户离线，channelId:{}", channelId);
        // 清除 Channel 和 User 绑定的对应关系
        webSocketService.offline(ctx);
        // 关闭 Channel 实现用户离线
        ctx.close();
    }
}
