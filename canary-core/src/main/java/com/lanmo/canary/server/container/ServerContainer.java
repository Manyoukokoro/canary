package com.lanmo.canary.server.container;

import com.lanmo.canary.spring.api.ServerConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by bowen on 2017/1/18.
 * server 容器
 */
public class ServerContainer {

    /**
     * 存放的容器 ConcurrentHashMap 线程安全的
     */
    private static final ConcurrentMap<String,ServerConfig> container=new ConcurrentHashMap<String, ServerConfig>();

    public static void addServerBean(ServerConfig serverBean){
        container.put(serverBean.getInterfaceId()+"/"+serverBean.getAlias(),serverBean);
    }

    public static <T> T getServer(ServerConfig serverBean,Class<T> clz){

        if (serverBean.getImpl() != null) {
            return (T) serverBean.getRef();
        }
        String key = serverBean.getInterfaceId() + "/" + serverBean.getAlias();
        serverBean = container.get(key);
        if(serverBean != null){
            return (T)serverBean.getRef();
        }
        return null;
    }



}
