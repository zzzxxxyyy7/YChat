package com.ychat.common.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@Configuration
public class NettyWebSocketServer {

    // websocket 监听端口
    private static final int WEB_SOCKET_PORT = 8090;

    // 自定义请求处理器
    public static final NettyWebSocketServerHandler NETTY_WEB_SOCKET_SERVER_HANDLER = new NettyWebSocketServerHandler();

    // 线程池执行器
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

    /**
     * 启动 ws server
     *
     * @return
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        run();
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        Future<?> future = bossGroup.shutdownGracefully();
        Future<?> future1 = workerGroup.shutdownGracefully();
        future.syncUninterruptibly();
        future1.syncUninterruptibly();
        log.info("关闭 ws server 成功");
    }

    private void run() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO)) // // 为 bossGroup 添加 日志处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 读空闲时间、写空闲时间、全局空闲时间、30 秒客户端没有向服务器发送心跳则关闭连接
                        // TODO 修改读空闲时间至合理时间
                        pipeline.addLast(new IdleStateHandler(600, 0, 0));
                        // 因为使用http协议，所以需要使用http的编码器，解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 以块方式写，添加 chunkedWriter 处理器
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * 说明：
                         *  1. http数据在传输过程中是分段的，HttpObjectAggregator可以把多个段聚合起来；
                         *  2. 这就是为什么当浏览器发送大量数据时，就会发出多次 http请求的原因
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        //保存用户ip
                        //pipeline.addLast(new HttpHeadersHandler());
                        /**
                         * 自定义 webSocket 握手处理器，用来处理 子协议 protocal 携带参数 token 进行登录验证
                         */
                        pipeline.addLast(new YChatShakeHandler());
                        // 自定义handler ，处理业务逻辑
                        pipeline.addLast(NETTY_WEB_SOCKET_SERVER_HANDLER);
                    }
                });
        // 启动服务器，监听端口，阻塞直到启动成功
        serverBootstrap.bind(WEB_SOCKET_PORT).sync();
    }
}
