package com.lanmo.canary.client.listener;

import com.lanmo.canary.client.transport.ClientTransport;
import com.lanmo.canary.netty.message.ResponseMsg;

/**
 * Created by bowen on 2017/1/18.
 * client端 监听response响应
 */
public interface ResponseListener {

    /**
     * response返回后的处理
     * @param clientTransport response所在的clientTransport
     * @param responseMsg 响应消息
     */
    void onResponse(ClientTransport clientTransport, ResponseMsg responseMsg);

}
