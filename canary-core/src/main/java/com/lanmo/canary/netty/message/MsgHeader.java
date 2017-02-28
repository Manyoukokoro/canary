package com.lanmo.canary.netty.message;

import lombok.Data;

/**
 * Created by bowen on 2017/1/17.
 * 请求头
 */
@Data
public class MsgHeader {

    /**
     * 消息ID
     */
    private long msgId;
    /**
     * 消息类型
     */
    private int msgType;

    /**
     * 消息体类型
     */
    private String clz;
    /**
     * body长度
     */
    private Integer length;

    public MsgHeader (){

    }

    public MsgHeader (int msgType){
         this.msgType=msgType;
    }
}
