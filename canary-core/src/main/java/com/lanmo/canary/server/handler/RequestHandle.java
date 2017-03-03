package com.lanmo.canary.server.handler;

import com.lanmo.canary.netty.message.MsgHeader;

import io.netty.channel.Channel;

/**
 * Created by bowen on 2017/1/18.
 * server端处理Request的处理器
 */
public interface RequestHandle {

     Object NULL=new Object();

    /**
     * 是否可处理
     * @param request
     * @return
     */
     boolean canHandle(Object request);



    /**
     * 处理
     * @param channel
     * @param req
     * @return
     */
    Object handleReq(MsgHeader msgHeader, Channel channel, Object req);


}
