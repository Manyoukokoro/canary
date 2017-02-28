package com.lanmo.canary.register;

/**
 * Created by bowen on 2017/1/18.
 */
public interface ServerRoute {

    /**
     * 注册服务
     * @param interfaceId
     * @param alisa
     */
    void register(String interfaceId, String alisa);

}
