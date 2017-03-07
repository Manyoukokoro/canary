package com.lanmo.canary.spring.parser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by bowen on 2017/3/3.
 */
public class CanaryServerBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element){
        return ServerBean.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder){

        String id=element.getAttribute("id");
        String interfaceId=element.getAttribute("interfaceId");
        String alias=element.getAttribute("alias");
        String impl=element.getAttribute("impl");
        Integer workerPoolSize=Integer.valueOf(element.getAttribute("workerPoolSize"));
        if(StringUtils.isBlank(id)){
            throw  new IllegalArgumentException("ant service id is not be null");
        }

        if(StringUtils.isBlank(interfaceId)){
            throw  new IllegalArgumentException("ant service interfaceId is not be null");
        }

        if(StringUtils.isBlank(alias)){
            throw  new IllegalArgumentException("ant service alias is not be null");
        }

        if(StringUtils.isBlank(impl)){
            throw  new IllegalArgumentException("ant service impl is not be null");
        }

        builder.addPropertyValue("id",id);
        builder.addPropertyValue("interfaceId",interfaceId);
        builder.addPropertyValue("alias",alias);
        builder.addPropertyValue("impl",impl);
        builder.addPropertyValue("workerPoolSize",workerPoolSize);
    }



}
