package com.lanmo.canary.spring.spring;

import com.lanmo.canary.spring.api.RegisterConfig;
import com.lanmo.canary.spring.factory.ClientRouteHandleFactory;
import com.lanmo.canary.spring.factory.ServerRouteHandleFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by bowen on 2017/2/22.
 *  解析 xml中的 register配置信息
 */
public class CanaryRegisterBeanDefinitionParser  extends AbstractSimpleBeanDefinitionParser{

    @Override
    protected Class<?> getBeanClass(Element element){
        return RegisterConfig.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder){

        String id=element.getAttribute("id");
        String protocol=element.getAttribute("protocol");
        String address=element.getAttribute("address");
        String root=element.getAttribute("root");
        String exportPort=element.getAttribute("exportPort");
        String timeOut=element.getAttribute("timeOut");
        String registerType=element.getAttribute("registerType");
        if(StringUtils.isBlank(id)){
            throw  new IllegalArgumentException("canary register id is not be null");
        }

        if(StringUtils.isBlank(protocol)){
            throw  new IllegalArgumentException("canary register protocol is not be null");
        }

        if(StringUtils.isBlank(address)){
            throw  new IllegalArgumentException("canary register address is not be null");
        }

        if(StringUtils.isBlank(root)){
            root="/canary";
        }

        if(StringUtils.isBlank(exportPort)){
            //默认发布的端口
            exportPort="2222";
        }

        if(StringUtils.isBlank(timeOut)){
            //默认超时时间
            timeOut="3000";
        }

        if(StringUtils.isBlank(registerType)){
            registerType="server";
        }
        //初始化
        if("server".equals(registerType)){
            ServerRouteHandleFactory.initServer(protocol,address,root,Integer.valueOf(timeOut),Integer.valueOf(exportPort));
        }else if("client".equals(registerType)){
            ClientRouteHandleFactory.initClient(protocol,address,root,Integer.valueOf(timeOut),Integer.valueOf(exportPort));
        }


        //注入到spring容器中
        builder.addPropertyValue("id",id);
        builder.addPropertyValue("protocol",protocol);
        builder.addPropertyValue("address",address);
        builder.addPropertyValue("root",root);
        builder.addPropertyValue("exportPort",exportPort);
        builder.addPropertyValue("timeOut",timeOut);


    }


}
