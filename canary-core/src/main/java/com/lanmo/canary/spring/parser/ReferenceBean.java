package com.lanmo.canary.spring.parser;

import com.lanmo.canary.spring.api.ReferenceConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by bowen on 2017/3/7.
 */
public class ReferenceBean<T> extends ReferenceConfig<T> implements FactoryBean,ApplicationContextAware,
        InitializingBean,DisposableBean {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private transient ApplicationContext applicationContext;

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

        logger.info("init client class");

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         this.applicationContext=applicationContext;
    }
}
