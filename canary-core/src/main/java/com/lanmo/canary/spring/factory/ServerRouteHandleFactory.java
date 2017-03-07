package com.lanmo.canary.spring.factory;

import com.lanmo.canary.register.ServerRoute;
import com.lanmo.canary.register.zookeeper.ZooKeeperServerHandler;
import com.lanmo.canary.server.Server;
import com.lanmo.canary.server.handler.RequestHandle;
import com.lanmo.canary.server.handler.ServerHandler;
import com.lanmo.canary.server.handler.impl.CanaryRequestHandle;
import com.lanmo.canary.spring.api.ServerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 2017/2/22.
 */
public class ServerRouteHandleFactory {

    private static Logger logger= LoggerFactory.getLogger(ServerRouteHandleFactory.class);
    private static ServerRoute serverRoute =null;
    private static volatile boolean initialized=false;
    private static Integer serverPort=null;

    private static Server server=null;

    private static String PROTOCOL_ZK="zookeeper",
            PROTOCOL_REDIS="redis";

    /**
     * 初始化链接
     * @param protocol
     * @param address
     * @param root
     * @param timeout
     * @param port
     */
    public static synchronized void initServer(String protocol,String address,String root,Integer timeout,Integer port){

        if(initialized){
            return;
        }
        if(PROTOCOL_ZK.equals(protocol)){
            logger.info("init zookeeper server ip is {},root is {},timeout is {},server port is {}",address,root,timeout,port);
            serverRoute=new ZooKeeperServerHandler(root,address,timeout);
            serverPort=port;

            List<RequestHandle> requestHandleList=new ArrayList<RequestHandle>();
            requestHandleList.add(new CanaryRequestHandle());
            server=new Server(serverRoute,new ServerHandler(requestHandleList),serverPort);
            initialized=true;
        }else {
            throw  new IllegalArgumentException("now canary only support register by zookeeper");
        }
    }


    public static synchronized void exportUrl(ServerConfig serverConfig){
        if(initialized){
            server.registerServer(serverConfig);
        }else {
            throw  new IllegalArgumentException("canary server not init");
        }

    }

}
