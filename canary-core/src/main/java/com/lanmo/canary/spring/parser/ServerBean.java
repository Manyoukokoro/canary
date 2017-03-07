package com.lanmo.canary.spring.parser;

import com.lanmo.canary.spring.api.RegisterConfig;
import com.lanmo.canary.spring.api.ServerConfig;

import com.lanmo.canary.spring.factory.ServerRouteHandleFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by bowen on 2017/3/3.
 */
public class ServerBean extends ServerConfig implements FactoryBean,ApplicationContextAware,
        InitializingBean,DisposableBean {

    private transient ApplicationContext applicationContext;


    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void destroy() throws Exception {
        destroy();
    }

    public Object getObject() throws Exception {
        return get();
    }

    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    public boolean isSingleton() {
        return true;
    }

    public void afterPropertiesSet() throws Exception {
        //获取需要暴露的实体类
        Object objectRef=applicationContext.getBean(getImpl());
        setRef(objectRef);
        if(ServerRouteHandleFactory.haveInit()){
            init();
        }else {
            //执行初始化方法
            RegisterConfig registerConfig= (RegisterConfig) applicationContext.getBean("serverRegister");
            ServerRouteHandleFactory.initServer(registerConfig.getProtocol(),registerConfig.getAddress(),registerConfig.getRoot(),
                    Integer.valueOf(registerConfig.getTimeOut()),Integer.valueOf(registerConfig.getExportPort()));
           init();
        }

    }

}
