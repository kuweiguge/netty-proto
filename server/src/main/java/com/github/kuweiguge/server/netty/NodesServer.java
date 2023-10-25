package com.github.kuweiguge.server.netty;

import com.github.kuweiguge.common.codec.MsgDecoder;
import com.github.kuweiguge.common.codec.MsgEncoder;
import com.github.kuweiguge.common.utils.Constant;
import com.github.kuweiguge.common.utils.NetworkUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 该server用于给各个微服务实例连接用。
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NodesServer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void startNettyServer(int port) throws Exception {
        // boss单线程
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NetworkUtil.useEpoll()? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 出来网络io事件，如记录日志、对消息编解码等
                    .childHandler(new ChildChannelHandler());
            // 绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                bossGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
                workerGroup.shutdownGracefully(1000, 3000, TimeUnit.MILLISECONDS);
            }));
            // 等待服务器监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("netty stop", e);
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * handler类
     */
    private static class ChildChannelHandler extends ChannelInitializer<Channel> {

        @Override
        protected void initChannel(Channel ch) {
            NodesServerHandler serverHandler = new NodesServerHandler();
            ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
            ch.pipeline()
                    .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))
                    .addLast(new MsgDecoder())
                    .addLast(new MsgEncoder())
                    .addLast(serverHandler);
        }
    }
}