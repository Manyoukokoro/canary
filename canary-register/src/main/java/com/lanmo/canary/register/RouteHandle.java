package com.lanmo.canary.register;


import java.util.List;

/**
 * Created by bowen on 2017/1/18.
 */
public interface RouteHandle {

    /**
     * 订阅节点变更
     * @param interfaceId
     * @param alisa
     */
    public void subscribeInterface(String interfaceId, String alisa);

    /**
     * 获取server列表
     * @param interfaceId
     * @param alisa
     * @return
     */
    List<Provider> route(String interfaceId, String alisa);

}
