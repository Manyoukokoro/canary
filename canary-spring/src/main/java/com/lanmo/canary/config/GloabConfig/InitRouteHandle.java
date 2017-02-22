package com.lanmo.canary.config.GloabConfig;

import com.lanmo.canary.register.RouteHandle;
import com.lanmo.canary.register.zookeeper.ZooKeeperRouteHandler;

/**
 * Created by bowen on 2017/2/22.
 */
public class InitRouteHandle {

    private  RouteHandle routeHandle =null;
    private static volatile boolean initialized=false;

    private static String PROTOCOL_ZK="zookeeper",PROTOCOL_REDIS="redis";

    public static synchronized void init(String protocol,String address,String root,Integer timeout){

        //if(PROTOCOL_ZK.equals(protocol)){
            ;
        //}

    }




}
