package com.ychat.common.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YChatShakeHandler extends ChannelInboundHandlerAdapter {

    /**
     * 如果在第一次 Http 握手就取得了 Token，直接手动升级协议为 WebSocket ，否则继续往下走，走完 WebSocket 的握手逻辑，再升级为 WebSocket (表明这是第一次升级)
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpObject httpObject = (HttpObject) msg;

        if (httpObject instanceof HttpRequest) {
            final HttpRequest req = (HttpRequest) httpObject;
            // 请求中取出 token
            HttpHeaders headers = req.headers();
            String token = headers.get("Sec-Websocket-Protocol");
            // 下方作为子协议参数传回 token
            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(ctx.pipeline(), req, req.getUri()), token, false);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);

            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ctx.pipeline().remove(this);
                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (!channelFuture.isSuccess()) {
                            ctx.fireExceptionCaught(channelFuture.cause());
                            log.info("握手失败: {}", token);
                        } else {
                            ctx.fireUserEventTriggered(WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                            log.info("握手成功: {}", token);
                        }
                    }
                });
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }

        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

}
