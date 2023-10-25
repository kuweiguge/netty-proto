package com.github.kuweiguge.client;

import com.github.kuweiguge.client.netty.NettyClient;
import com.github.kuweiguge.common.message.DemoMsg;
import com.github.kuweiguge.common.message.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * @author zhengwei
 * @version 1.0
 * @since 2023/10/25 10:41
 */
public class Main {
    public static void main(String[] args) {
        try {
            Bootstrap bootstrap = NettyClient.getInstance().getBootstrap();
            ChannelFuture channelFuture1 = bootstrap.connect("127.0.0.1", 11111).sync();
            Channel channel1 = channelFuture1.channel();
            while (true){
                Thread.sleep(10);
                DemoMsg msg = new DemoMsg();
                msg.setAppName("test");
                msg.setMessageType(MessageType.REQUEST_NEW_KEY);
                channel1.writeAndFlush(msg).sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}