package com.lanmo.canary.spring.parser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by bowen on 2017/2/7.
 */
public class CanaryNamespceHandler extends NamespaceHandlerSupport {


    public void init() {
        registerBeanDefinitionParser("reference",new CanaryReferenceBeanDefinitionParser());
        registerBeanDefinitionParser("server",new CanaryServerBeanDefinitionParser());
        registerBeanDefinitionParser("register",new CanaryRegisterBeanDefinitionParser());
    }



}
