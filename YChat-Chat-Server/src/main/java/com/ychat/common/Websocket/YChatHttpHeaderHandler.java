package com.ychat.common.Websocket;

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public class YChatHttpHeaderHandler extends ChannelInboundHandlerAdapter {

    /**
     * 如果在第一次 Http 握手就取得了 Token，直接手动升级协议为 WebSocket ，否则继续往下走，走完 WebSocket 的握手逻辑，再升级为 WebSocket (表明这是第一次升级)
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            Optional<String> tokenOptional
                    = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);

            // 如果首次 Http 握手 请求头存在 token ，绑定到 Channel
            tokenOptional.ifPresent(s -> NettyUtils.setAttr(ctx.channel(), NettyUtils.USER_TOKEN, s));
            // 重新设置 url 以适配 ws 路径要求
            request.setUri(urlBuilder.getPath().toString());
            String userIp = request.headers().get("X-Real-IP");
            if (StringUtils.isBlank(userIp)) {
                InetSocketAddress address =  (InetSocketAddress) ctx.channel().remoteAddress();
                userIp = address.getAddress().getHostAddress();
            }
            // 绑定 ip 到 channel
            NettyUtils.setAttr(ctx.channel(), NettyUtils.USER_IP, userIp);
            // 移除自身处理器
            ctx.pipeline().remove(this);
        }

        ctx.fireChannelRead(msg);
    }

}
