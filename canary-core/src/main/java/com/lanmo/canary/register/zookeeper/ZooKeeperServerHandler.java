package com.lanmo.canary.register.zookeeper;

import com.lanmo.canary.common.exception.CanaryException;
import com.lanmo.canary.register.ServerRoute;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;

import io.netty.util.internal.ConcurrentSet;

/**
 * Created by bowen on 2017/1/18.
 */
public class ZooKeeperServerHandler extends ZooKeeperRouteHandler implements ServerRoute {

    private final ConcurrentSet<String> infRegisterSet = new ConcurrentSet<String>();

    private boolean serverFailFast = true;

    private String serverHost;

    private Integer port;

    public ZooKeeperServerHandler(String root, String serverString, int timeout,Integer port) {
        super(root, serverString, timeout);
        this.serverHost = getLocalHost();
        this.port=port;
        //开启一个扫描线程,反复检查自己是否在zookeeper上
        checkConnections2ZooK();
    }


    public void register(String interfaceId, String alisa) {
        String dataPath = getDataPath(interfaceId, alisa);
        logger.info("export interface {},alisa {},real path is {}",interfaceId,alisa,dataPath);
        if(infRegisterSet.contains(dataPath)){
            return;
        }
        infRegisterSet.add(dataPath);
        if(!zkClient.exists(dataPath)){
            zkClient.createPersistent(dataPath, true);
        }
        createServerNode(dataPath);
    }

    private void createServerNode(String dataPath) {
        String targetPath = dataPath +"/"+getLocalHostPort();
        try {
            if(!zkClient.exists(targetPath)) {
                zkClient.createEphemeral(targetPath);
            }
        }catch (Exception e){
            logger.warn("创建节点失败", e);
        }
    }

    private void checkConnections2ZooK(){
        Thread serverRecconet = new Thread(new Runnable() {

            public void run() {
                int sleepTime = 10 * 60 * 1000;
                for (;;) {
                    try {
                        Thread.sleep(sleepTime);
                        reconnect();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, "server端巡检zookeeper是否有自己的线程");
        serverRecconet.setDaemon(true);
        serverRecconet.start();
    }

    private void reconnect() {
        for (String dataPath : infRegisterSet) {
            if (!zkClient.exists(dataPath)) {
                createServerNode(dataPath);
            }

            List<String> providers = zkClient.getChildren(dataPath);
            if (!new HashSet<String>(providers).contains(getLocalHostPort())) {
                createServerNode(dataPath);
            }

        }
    }

    private String getLocalHostPort() {
        return serverHost + ":"+port ;
    }


    private String getLocalHost() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            return addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        logger.error("无法获取本机ip");
        if (serverFailFast) {
            throw new CanaryException("无法获取本机ip,创建server失败");
        }
        return null;
    }

    public boolean isServerFailFast() {
        return serverFailFast;
    }

    public void setServerFailFast(boolean serverFailFast) {
        this.serverFailFast = serverFailFast;
    }
}
