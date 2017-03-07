package com.lanmo.canary.client.factory;

import com.lanmo.canary.client.Client;
import com.lanmo.canary.register.RouteHandle;
import com.lanmo.canary.spring.api.ReferenceConfig;

/**
 * Created by bowen on 2017/1/22.
 * 构建client
 */
public class ClientFactory {

    public static Client buildClient(ReferenceConfig referenceConfig, RouteHandle routeHandle) {
        return new Client(referenceConfig, routeHandle);
    }


}
