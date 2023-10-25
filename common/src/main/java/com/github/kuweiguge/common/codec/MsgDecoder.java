package com.github.kuweiguge.common.codec;

import com.github.kuweiguge.common.message.DemoMsg;
import com.github.kuweiguge.common.utils.ProtostuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-07-29
 */
public class MsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {
        try {
            byte[] body = new byte[in.readableBytes()];
            in.readBytes(body);
            list.add(ProtostuffUtils.deserialize(body, DemoMsg.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}