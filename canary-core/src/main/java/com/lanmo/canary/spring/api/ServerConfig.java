package com.lanmo.canary.spring.api;

import com.lanmo.canary.spring.factory.ServerRouteHandleFactory;

import java.util.Map;

import lombok.Data;

/**
 * Created by Administrator on 2017/2/21.
 * 服务器端 暴露服务的配置
 */
@Data
public class ServerConfig {

    private String id;
    private String interfaceId;
    private String impl;
    private String alias;
    private int workerPoolSize = 200;
    private Map<String, Object> params;
    private Class<?> interfaceClass;
    private transient volatile Object ref;
    private transient volatile boolean initialized;
    private transient volatile boolean destroy;


    protected Object get(){
            return ref;
    }

    protected void init(){
        if(destroy){
            return;
        }
        if(initialized){
            return;
        }
        //暴露服务
         ServerRouteHandleFactory.exportUrl(getServerConfig());

        initialized=true;
    }

    private void destroy() throws Exception{
        ref=null;
        destroy=true;
    }

    private ServerConfig getServerConfig(){
        ServerConfig serverConfig=new ServerConfig();
        serverConfig.setInterfaceId(this.interfaceId);
        serverConfig.setRef(ref);
        serverConfig.setAlias(alias);
        return serverConfig;
    }

}
