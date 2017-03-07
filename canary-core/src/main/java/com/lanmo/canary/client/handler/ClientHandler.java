package com.lanmo.canary.client.handler;

import com.lanmo.canary.client.listener.ResponseListener;
import com.lanmo.canary.client.listener.impl.CanaryResponseListener;
import com.lanmo.canary.client.transport.impl.DefaultClientTransport;
import com.lanmo.canary.netty.message.RequestMsg;
import com.lanmo.canary.netty.message.ResponseMsg;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by bowen on 2017/1/20.
 */

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private DefaultClientTransport clientTransport;

    private List<ResponseListener> responseListenerList = new ArrayList<ResponseListener>();

    public ClientHandler() {
        responseListenerList.add(new CanaryResponseListener());
    }

    public ClientHandler(List<ResponseListener> responseListenerList) {
        this.responseListenerList = responseListenerList;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        if (msg instanceof ResponseMsg) {
            ResponseMsg responseMsg = (ResponseMsg) msg;
            //responseMsg不引用byteBuf，方便gc
            responseMsg.setMsgBody(null);

            for(ResponseListener responseListener: responseListenerList){
                responseListener.onResponse(clientTransport, responseMsg);
            }
        } else if (msg instanceof RequestMsg) {
            //receive the callback ResponseMessage
            RequestMsg responseMsg = (RequestMsg) msg;
            //find the transport
        }
    }

    public void setClientTransport(DefaultClientTransport clientTransport) {
        this.clientTransport = clientTransport;
    }

    public void setResponseListenerList(List<ResponseListener> responseListenerList) {
        this.responseListenerList = responseListenerList;
    }

    public synchronized void addListener(ResponseListener responseListener){
        this.responseListenerList.add(responseListener);
    }
}
