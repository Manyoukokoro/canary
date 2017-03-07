package com.lanmo.canary.spring.api;

import com.lanmo.canary.client.Client;
import com.lanmo.canary.client.factory.ClientFactory;
import com.lanmo.canary.client.factory.ProxyFactory;
import com.lanmo.canary.spring.factory.ClientRouteHandleFactory;

import java.util.Map;

import lombok.Data;

/**
 * Created by Administrator on 2017/2/21.
 * client 引用的配置
 */
@Data
public class ReferenceConfig<T> {

    private String id;
    private String interfaceId;
    private String alias;
    private String protocol;
    private int timeout;
    private boolean lazy = false;

    private RequestMethod requestMethod;

    private Class<?> interfaceClass;
    /**
     * ant自定义参数
     */
    private Map<String, Object> params;

    // 接口代理类引用
    private transient volatile T ref;

    private transient volatile boolean initialized;

    private transient volatile boolean destroyed;


    public boolean isLazy(){
        return  this.lazy;
    }


    public synchronized T get() {
        if (destroyed){
            throw new IllegalStateException("Already destroyed!");
        }
        if (ref == null) {
            init();
        }
        return ref;
    }

    public synchronized void destroy() {
        if (ref == null) {
            return;
        }
        if (destroyed){
            return;
        }
        destroyed = true;
        ref = null;
    }

    /**
     * 初始化
     */
    private void init(){

        if (initialized) {
            return;
        }
        initialized = true;
        if (interfaceId == null || interfaceId.length() == 0) {
            throw new IllegalStateException("<canary:reference interface=\"\" /> interface not allow null!");
        }

        try {
            interfaceClass=Class.forName(interfaceId, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("<canary:reference interface="+interfaceId+" /> interface not find!");
        }

        try {
            ref= (T) ProxyFactory.buildProxy(this.interfaceClass, ClientRouteHandleFactory.getDefaultClient(genConsumerBean()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ReferenceConfig genConsumerBean(){
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setInterfaceId(this.interfaceId);
        referenceConfig.setAlias(this.alias);
        referenceConfig.setTimeout(this.timeout);
        return referenceConfig;
    }


}
