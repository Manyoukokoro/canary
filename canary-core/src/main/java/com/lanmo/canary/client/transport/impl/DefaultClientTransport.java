package com.lanmo.canary.client.transport.impl;

import com.lanmo.canary.client.factory.ClientTransportFactory;
import com.lanmo.canary.client.future.MsgFuture;
import com.lanmo.canary.client.transport.ClientTransport;
import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.common.exception.CanaryException;
import com.lanmo.canary.netty.message.BaseMsg;
import com.lanmo.canary.netty.message.RequestMsg;
import com.lanmo.canary.netty.message.ResponseMsg;
import com.lanmo.canary.register.Provider;
import com.lanmo.canary.spring.api.ReferenceConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * Created by bowen on 2017/1/20.
 */
@Data
public class DefaultClientTransport implements ClientTransport{


    private Logger logger = LoggerFactory.getLogger(DefaultClientTransport.class);

    private int timeout = 2000;

    private ReferenceConfig referenceConfig;

    private Provider provider;

    private Channel channel;

    private AtomicLong requestMsgId = new AtomicLong(1);

    private final ConcurrentHashMap<Long, MsgFuture> futureMap = new ConcurrentHashMap<Long, MsgFuture>();



    public void reconnect() {
        boolean isOpen=this.channel!=null&&this.channel.isActive()&&this.channel.isOpen();
        if(!isOpen){
            //重试
            try{
                Channel channel= ClientTransportFactory.buildChannel(provider);
                this.setChannel(channel);
                ClientTransportFactory.buildTransport(this);
            }catch (Exception e){
                e.printStackTrace();
                logger.error("connect error:cause {}",e.getMessage());
            }
        }
    }

    public void shutdown() {
        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public ResponseMsg sendMsg(BaseMsg baseMsg) {
        baseMsg.getMsgHeader().setMsgId(requestMsgId.getAndIncrement());
         //设置msg类型
        if(baseMsg instanceof RequestMsg){
            baseMsg.getMsgHeader().setMsgType(CanaryConstants.REQUEST_MSG);
        }else if(baseMsg instanceof ResponseMsg){
            baseMsg.getMsgHeader().setMsgType(CanaryConstants.RESPONSE_MSG);
        }

        MsgFuture<ResponseMsg> msgFuture = this.sendAsync(baseMsg);
        try {
            return msgFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.futureMap.remove(baseMsg.getMsgHeader().getMsgId());
            throw new CanaryException("线程中断",e);
        } catch (CanaryException e) {
            this.futureMap.remove(baseMsg.getMsgHeader().getMsgId());
            throw e;
        }
    }

    private MsgFuture<ResponseMsg> sendAsync(BaseMsg baseMsg) {
        MsgFuture<ResponseMsg> msgFuture = new MsgFuture<ResponseMsg>(baseMsg, timeout);
        futureMap.put(baseMsg.getMsgId(), msgFuture);
        if (baseMsg instanceof RequestMsg) {
            channel.writeAndFlush(baseMsg, channel.voidPromise());
        }
        msgFuture.setSendTime(System.currentTimeMillis());
        return msgFuture;
    }

    public void success(ResponseMsg responseMsg) {
        MsgFuture msgFuture = futureMap.get(responseMsg.getMsgId());
        if(msgFuture == null){
            logger.warn("消息id:{} 已经被丢弃", responseMsg.getMsgId());
            return;
        }
        if (!msgFuture.isDone()) {
            msgFuture.setResult(responseMsg);
        }
    }
}
