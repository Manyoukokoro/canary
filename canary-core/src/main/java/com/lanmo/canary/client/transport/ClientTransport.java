package com.lanmo.canary.client.transport;

import com.lanmo.canary.netty.message.BaseMsg;
import com.lanmo.canary.netty.message.ResponseMsg;

/**
 * Created by bowen on 2017/1/18.
 */
public interface ClientTransport {

    void reconnect();

    void shutdown();

    ResponseMsg sendMsg(BaseMsg baseMsg);

    void success(ResponseMsg responseMsg);

}
