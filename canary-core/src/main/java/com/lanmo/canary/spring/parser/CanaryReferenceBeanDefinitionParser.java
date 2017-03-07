package com.lanmo.canary.spring.parser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by bowen on 2017/2/7.
 */
public class CanaryReferenceBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element){
        return ReferenceBean.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder){

        String id=element.getAttribute("id");
        String interfaceId=element.getAttribute("interfaceId");
        String alias=element.getAttribute("alias");
        int timeout=Integer.valueOf(element.getAttribute("timeout")==null?
                "2000":element.getAttribute("timeout"));
        boolean lazy=Boolean.valueOf(element.getAttribute("lazy")==null?"false":
                        element.getAttribute("lazy"));
        if(StringUtils.isBlank(id)){
            throw  new IllegalArgumentException("canary reference id is not be null");
        }

        if(StringUtils.isBlank(interfaceId)){
            throw  new IllegalArgumentException("canary reference interfaceId is not be null");
        }

        if(StringUtils.isBlank(alias)){
            throw  new IllegalArgumentException("canary reference alias is not be null");

        }

        builder.addPropertyValue("id",id);
        builder.addPropertyValue("interfaceId",interfaceId);
        builder.addPropertyValue("alias",alias);
        builder.addPropertyValue("timeout",timeout);
        builder.addPropertyValue("lazy",lazy);
    }

}
