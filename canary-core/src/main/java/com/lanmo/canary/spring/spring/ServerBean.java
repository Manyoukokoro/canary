package com.lanmo.canary.spring.spring;

import com.lanmo.canary.spring.api.ServerConfig;

import org.springframework.beans.BeansException;
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
        init();
    }

}
