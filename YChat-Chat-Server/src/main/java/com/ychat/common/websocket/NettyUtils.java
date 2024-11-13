package com.ychat.common.websocket;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public class NettyUtils {

    public static final AttributeKey<String> USER_TOKEN = AttributeKey.valueOf("token");

    public static final AttributeKey<String> USER_IP = AttributeKey.valueOf("userIp");

    public static <T> void setAttr(Channel ctx, AttributeKey<T> key, T value) {
        Attribute<T> attr = ctx.attr(key);
        attr.set(value);
    }

    public static <T> T getAttr(Channel ctx, AttributeKey<T> key) {
        Attribute<T> attr = ctx.attr(key);
        return attr.get();
    }
}
