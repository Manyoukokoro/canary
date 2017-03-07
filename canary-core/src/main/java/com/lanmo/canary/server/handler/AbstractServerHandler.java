package com.lanmo.canary.server.handler;

import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.netty.message.MsgHeader;
import com.lanmo.canary.netty.message.ResponseMsg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by bowen on 2017/1/19.
 */
public abstract class AbstractServerHandler  extends ChannelInboundHandlerAdapter{

    private static final Logger logger= LoggerFactory.getLogger(AbstractServerHandler.class);


    /**
     * 发送 response
     * @param channel
     * @param msgResult
     */
    protected void sendResponse(Channel channel,Object msgResult){

        MsgHeader msgHeader=new MsgHeader(CanaryConstants.RESPONSE_MSG);
        if(msgResult!=null){
            msgHeader.setClz(msgResult.getClass().getName());
        }else {
            msgHeader.setClz(CanaryConstants.NULL_RESULT_CLASS);
        }
        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setReceiveTime(System.currentTimeMillis());
        responseMsg.setResponse(msgResult);
        responseMsg.setMsgHeader(msgHeader);
        channel.writeAndFlush(responseMsg, channel.voidPromise());
    }


    /**
     * 带msgId的发送response
     * @param msgId
     * @param channel
     * @param msgResult
     */
    protected void sendResponse(long msgId,Channel channel,Object msgResult){

        MsgHeader msgHeader=new MsgHeader(CanaryConstants.RESPONSE_MSG);
        if(msgResult!=null){
            msgHeader.setClz(msgResult.getClass().getName());
        }else {
            msgHeader.setClz(CanaryConstants.NULL_RESULT_CLASS);
        }

        ResponseMsg responseMsg = new ResponseMsg();
        responseMsg.setReceiveTime(System.currentTimeMillis());
        responseMsg.setResponse(msgResult);
        responseMsg.setMsgHeader(msgHeader);
        responseMsg.getMsgHeader().setMsgId(msgId);

        channel.writeAndFlush(responseMsg, channel.voidPromise());
    }

}
