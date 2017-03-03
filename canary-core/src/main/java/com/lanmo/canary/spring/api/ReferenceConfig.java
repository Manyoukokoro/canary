package com.lanmo.canary.spring.api;

import java.util.Map;

import lombok.Data;

/**
 * Created by Administrator on 2017/2/21.
 * client 引用的配置
 */
@Data
public class ReferenceConfig {

    private String id;
    private String interfaceId;
    private String alias;
    private String protocol;
    private int timeout;
    private boolean lazy = false;

    private RequestMethod requestMethod;
    /**
     * ant自定义参数
     */
    private Map<String, Object> params;

    public boolean isLazy(){
        return  this.lazy;
    }


}
