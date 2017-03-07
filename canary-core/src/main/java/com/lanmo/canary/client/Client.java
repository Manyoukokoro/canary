package com.lanmo.canary.client;

import com.lanmo.canary.client.factory.ClientTransportFactory;
import com.lanmo.canary.client.transport.Connection;
import com.lanmo.canary.client.transport.impl.DefaultClientTransport;
import com.lanmo.canary.common.exception.CanaryException;
import com.lanmo.canary.netty.message.BaseMsg;
import com.lanmo.canary.netty.message.ResponseMsg;
import com.lanmo.canary.register.Provider;
import com.lanmo.canary.register.RouteHandle;
import com.lanmo.canary.spring.api.ReferenceConfig;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * Created by bowen on 2017/1/18.
 */
@Data
public class Client {

    private Logger logger = LoggerFactory.getLogger(Client.class);
    private ReferenceConfig referenceConfig;
    private List<Connection> connectionList = new ArrayList<Connection>();
    private ReentrantLock lock = new ReentrantLock();
    boolean hasInit = false;

    private RouteHandle routeHandle;

    public Client(ReferenceConfig referenceConfig, RouteHandle routeHandle) {
        this.referenceConfig = referenceConfig;
        this.routeHandle = routeHandle;
        if(routeHandle != null) {
            routeHandle.subscribeInterface(referenceConfig.getInterfaceId(), referenceConfig.getAlias());
        }
        if(referenceConfig!=null && !referenceConfig.isLazy()) {
            init();
        }
    }

    /**
     * 通过反射或javassist直接调用
     * @param baseMsg
     * @return
     * @throws Exception
     */
    public Object invoke(BaseMsg baseMsg) throws Exception {
        if(!hasInit){
            init();
        }

        if(CollectionUtils.isEmpty(connectionList)){
            throw new CanaryException("没有可用的连接");
        }
        try {
            //这里要做 负载策略
            Connection connection = connectionList.get(0);
            ResponseMsg responseMsg = connection.getClientTransport().sendMsg(baseMsg);
            Object responseResult = responseMsg.getResponse();
            if(responseResult instanceof CanaryException){
                throw new CanaryException("");
            }
            return responseResult;
        }catch (Exception e){
            throw e;
        }
    }

    public void init() {
        lock.lock();
        try {
            logger.info("get this interface {},alias {}",referenceConfig.getInterfaceId(), referenceConfig.getAlias());
            List<Provider> providerList = routeHandle.route(referenceConfig.getInterfaceId(), referenceConfig.getAlias());
            if(CollectionUtils.isEmpty(providerList)){
                return;
            }
            for (Provider provider : providerList) {
                Connection connection = buildConnection(provider);
                if (connection == null) {
                    logger.warn("provider is invalid,host--{},port--{}", provider.getHost(), provider.getPort());
                } else {
                    connectionList.add(connection);
                }
            }
        }finally {
            hasInit = true;
            lock.unlock();
        }
    }

    private Connection buildConnection(Provider provider) {
        try {
            Channel channel = ClientTransportFactory.buildChannel(provider);
            DefaultClientTransport clientTransport = ClientTransportFactory.buildTransport(provider, channel);
            clientTransport.setReferenceConfig(this.referenceConfig);
            Connection connection = new Connection();
            connection.setProvider(provider);
            connection.setClientTransport(clientTransport);
            logger.info("connect to server success,host--{},port--{}", provider.getHost(), provider.getPort());
            return connection;
        }catch (Exception e){
            logger.error("something is wrong on buildConnection",e);
        }
        return null;
    }


}
