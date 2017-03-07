package com.lanmo.canary.client.factory;

import com.lanmo.canary.client.handler.ClientHandler;
import com.lanmo.canary.client.initializer.ClientChannelInitializer;
import com.lanmo.canary.client.listener.ResponseListener;
import com.lanmo.canary.client.listener.impl.CanaryResponseListener;
import com.lanmo.canary.client.transport.ClientTransport;
import com.lanmo.canary.client.transport.impl.DefaultClientTransport;
import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.common.exception.CanaryException;
import com.lanmo.canary.register.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by bowen on 2017/1/22.
 */
public class ClientTransportFactory {

    private static final Logger logger = LoggerFactory.getLogger(ClientTransportFactory.class);


    /**
     * 构建client的channel
     * @param host
     * @param port
     * @return
     */
    public static Channel buildChannel(final String host,final int port){
        Channel channel=null;

        Bootstrap b=new Bootstrap();
        b.group(new NioEventLoopGroup()).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE,true);

        List<ResponseListener> responseListeners=new ArrayList<ResponseListener>();
        responseListeners.add(new CanaryResponseListener());
        //构建handler
        ClientHandler clientHandler=new ClientHandler(responseListeners);
        b.handler(new ClientChannelInitializer(clientHandler));

        try {
            ChannelFuture f=b.connect(host,port).sync();
            if (f.isSuccess()) {
                channel = f.channel();
                logger.info("connect to server success,host--{},port--{}", host, port);
            } else {
                throw new CanaryException("建立连接失败");
            }

        }catch (Exception e){
           e.printStackTrace();
            logger.error(" content server error:{}",e.getMessage());
        }
        return channel;
    }


    /**
     * 构建client的channel
     * @param provider
     * @return
     */
    public static Channel buildChannel(final Provider provider){
        return buildChannel(provider.getHost(),provider.getPort());
    }

    public static DefaultClientTransport buildTransport(String host, int port, Channel channel) {
        Provider provider =new Provider();
        provider.setHost(host);
        provider.setPort(port);
        return buildTransport(provider,channel);
    }

    public static DefaultClientTransport buildTransport(Provider provider, Channel channel) {
        if(provider == null){
            throw new IllegalArgumentException("provider must not be null!");
        }
        DefaultClientTransport clientTransport = new DefaultClientTransport();
        clientTransport.setChannel(channel);
        clientTransport.setProvider(provider);
        ClientHandler clientHandler = (ClientHandler) channel.pipeline().get(CanaryConstants.CLIENT_HANDLER);
        clientHandler.setClientTransport(clientTransport);
        return clientTransport;
    }

    public static ClientTransport buildTransport(DefaultClientTransport clientTransport) {
        ClientHandler clientHandler = (ClientHandler) clientTransport.getChannel().pipeline().get(CanaryConstants.CLIENT_HANDLER);
        clientHandler.setClientTransport(clientTransport);
        return clientTransport;
    }




}
