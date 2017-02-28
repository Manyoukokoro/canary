package com.lanmo.canary.spring.api;

import lombok.Data;

/**
 * Created by Administrator on 2017/2/21.
 * 解析 xml中的 register配置--注册中心的配置信息
 */

@Data
public class RegisterConfig {

    private String id;
    /**
     * 暴露协议 目前只支持 zookeeper
     */
    private String protocol;
    /**
     * 注册服务的 地址 例如 zookeeper 的 "127.0.0.1:2181"
     */
    private String address;
    /**
     * 根路径 --默认是 canary
     */
    private String root="/canary";
    /**
     * 服务器对外暴露的地址 默认是2222
     */
    private Integer exportPort=2222;
    /**
     * 超时时间
     */
    private Integer timeOut;

    /**
     * 注册的是服务端 还是 client端  server表示 服务器端  client 表示客户端
     */
    private String registerType="server";


}
