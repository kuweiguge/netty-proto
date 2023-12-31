package com.github.kuweiguge.client.netty;

import com.github.kuweiguge.common.codec.MsgDecoder;
import com.github.kuweiguge.common.codec.MsgEncoder;
import com.github.kuweiguge.common.utils.Constant;
import com.github.kuweiguge.common.utils.NetworkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * netty连接器
 *
 * @author wuweifeng wrote on 2019-11-05.
 */
public class NettyClient {
    private static final NettyClient nettyClient = new NettyClient();

    private Bootstrap bootstrap;

    public static NettyClient getInstance() {
        return nettyClient;
    }

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    private NettyClient() {
        if (bootstrap == null) {
            bootstrap = initBootstrap();
        }
    }

    private Bootstrap initBootstrap() {
        // 少线程
        EventLoopGroup group = new NioEventLoopGroup(2);

        Bootstrap bootstrap = new Bootstrap();
        NettyClientHandler nettyClientHandler = new NettyClientHandler();
        bootstrap.group(group).channel(NetworkUtil.useEpoll() ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ByteBuf delimiter = Unpooled.copiedBuffer(Constant.DELIMITER.getBytes());
                        ch.pipeline()
                                .addLast(new DelimiterBasedFrameDecoder(Constant.MAX_LENGTH, delimiter))
                                .addLast(new MsgDecoder())
                                .addLast(new MsgEncoder())
                                // 10秒没消息时，就发心跳包过去
                                .addLast(new IdleStateHandler(0, 0, 30))
                                .addLast(nettyClientHandler);
                    }
                });
        return bootstrap;
    }
}