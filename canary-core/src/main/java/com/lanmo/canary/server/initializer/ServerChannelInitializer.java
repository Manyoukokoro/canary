package com.lanmo.canary.server.initializer;

import com.lanmo.canary.netty.codec.CanaryDecoder;
import com.lanmo.canary.netty.codec.CanaryEncoder;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by bowen on 2017/1/20.
 */
@ChannelHandler.Sharable
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    private ChannelInboundHandlerAdapter serverChannelHandler;

    public ServerChannelInitializer(ChannelInboundHandlerAdapter serverChannelHandler) {
        this.serverChannelHandler = serverChannelHandler;
    }

    /**
     * 添加 协议的 和server的解析过程
     * @param ch
     * @throws Exception
     */
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new CanaryDecoder(),new CanaryEncoder(),this.serverChannelHandler);
    }

}
