package com.lanmo.canary.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * Created by bowen on 2017/1/17.
 * 基础消息
 */
@Data
public class BaseMsg {

    /**
     * 请求头
     */
    private MsgHeader msgHeader;
    /**
     * 消息体
     */
    private ByteBuf msgBody;

    private transient Channel channel;


    public long getMsgId() {
        return msgHeader.getMsgId();
    }

    public void setMsgId(long msgId) {
        this.getMsgHeader().setMsgId(msgId);
    }

}
