package com.lanmo.canary.client.listener.impl;

import com.lanmo.canary.client.listener.ResponseListener;
import com.lanmo.canary.client.transport.ClientTransport;
import com.lanmo.canary.netty.message.ResponseMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by bowen on 2017/1/18.
 */
public class CanaryResponseListener implements ResponseListener {

    Logger logger= LoggerFactory.getLogger(this.getClass());

    public void onResponse(ClientTransport clientTransport, ResponseMsg responseMsg) {
       logger.debug("success receive response");
        clientTransport.success(responseMsg);
    }


}
