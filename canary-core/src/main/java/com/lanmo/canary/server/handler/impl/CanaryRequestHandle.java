package com.lanmo.canary.server.handler.impl;


import com.lanmo.canary.common.utils.ReflectionUtils;
import com.lanmo.canary.netty.message.MsgHeader;
import com.lanmo.canary.server.container.ServerContainer;
import com.lanmo.canary.server.handler.RequestHandle;
import com.lanmo.canary.spring.api.ReferenceConfig;
import com.lanmo.canary.spring.api.ServerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import io.netty.channel.Channel;

/**
 * Created by bowen on 2017/1/18.
 * 服务端处理
 */
public class CanaryRequestHandle  implements RequestHandle {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    public boolean canHandle(Object request) {
        return request!=null&&request instanceof ReferenceConfig;
    }

    public Object handleReq(MsgHeader msgHeader, Channel channel, Object req) {
        return invoke((ReferenceConfig)req);
    }

    protected Object invoke(ReferenceConfig referenceConfig){

        try {
            //获取class信息
            Class targetClz=Class.forName(referenceConfig.getInterfaceId());
            //获得实例
            Object instance=getSeverInstanceByConsumer(referenceConfig,targetClz);
            //获得方法名字
            String methodName=referenceConfig.getRequestMethod().getMethod();
            //获取参数信息
            Object[] params = referenceConfig.getRequestMethod().getMethodParams();
            Class[] parameterTypes = new Class[params.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                String type = referenceConfig.getRequestMethod().getParameterTypes()[i];
                //获得参数
                parameterTypes[i] = ReflectionUtils.forName(type);
            }
            Method method = targetClz.getMethod(methodName, parameterTypes);
            return method.invoke(instance, params);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("执行方法异常 接口:{},method:{}", referenceConfig.getInterfaceId(),
                    referenceConfig.getRequestMethod().getMethod(), e.getMessage());
            return null;
        }
    }


    /**
     * 获取server端的实例
     * @param referenceConfig
     * @param clz
     * @param <T>
     * @return
     */
    protected <T> T getSeverInstanceByConsumer(ReferenceConfig referenceConfig,Class<T> clz){
        ServerConfig severBean=new ServerConfig();
        severBean.setInterfaceId(referenceConfig.getInterfaceId());
        severBean.setAlias(referenceConfig.getAlias());
        return ServerContainer.getServer(severBean,clz);
    }

}
