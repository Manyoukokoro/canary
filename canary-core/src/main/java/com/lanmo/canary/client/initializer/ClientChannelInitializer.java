package com.lanmo.canary.client.initializer;

import com.lanmo.canary.client.handler.ClientHandler;
import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.netty.codec.CanaryDecoder;
import com.lanmo.canary.netty.codec.CanaryEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by bowen on 2017/1/22.
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ClientHandler clientHandler;

    public ClientChannelInitializer(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CanaryEncoder());
        pipeline.addLast(new CanaryDecoder());
        pipeline.addLast(CanaryConstants.CLIENT_HANDLER, clientHandler);
    }

}
