package com.lanmo.canary.server;

import com.lanmo.canary.register.ServerRoute;
import com.lanmo.canary.server.container.ServerContainer;
import com.lanmo.canary.server.initializer.ServerChannelInitializer;
import com.lanmo.canary.spring.api.ServerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by bowen on 2017/1/18.
 * 构建server的服务
 */
public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private int port;

    private ServerRoute serverRoute;

    private ChannelInboundHandlerAdapter serverHandler;

    private NioEventLoopGroup group=new NioEventLoopGroup(200);

    /**
     * 构造server
     * @param serverRoute
     * @param serverHandler
     * @param port
     */
    public Server(ServerRoute serverRoute, ChannelInboundHandlerAdapter serverHandler, int port) {
        this.serverRoute = serverRoute;
        this.serverHandler = serverHandler;
        this.port = port;
    }

    /**
     * 注册服务
     * @param serverBean
     */
    public void registerServer(ServerConfig serverBean){
        ServerContainer.addServerBean(serverBean);
        serverRoute.register(serverBean.getInterfaceId(),serverBean.getAlias());
    }


    public void setWorkerGroup(NioEventLoopGroup workerGroup) {
        this.group = workerGroup;
    }


    public void run() throws Exception{
        //主线程组---接收请求的
        final NioEventLoopGroup boosGroup=new NioEventLoopGroup();
        //工作组---处理请求的
        final NioEventLoopGroup workerGroup=this.group;

        try{
            ServerBootstrap b=new ServerBootstrap();

            b.group(boosGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer(this.serverHandler))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE,Boolean.TRUE);

            ChannelFuture f=b.bind(port).sync();

            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        //logger success
                        logger.info("start server success, port:{}",port);
                    }else{
                        boosGroup.shutdownGracefully();
                        workerGroup.shutdownGracefully();
                        logger.error("start server failed");
                    }
                }
            });
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }

    }
}
