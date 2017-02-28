package com.lanmo.canary.netty.message;

import lombok.Data;

/**
 * Created by bowen on 2017/1/17.
 *
 * 封装request消息体
 *
 */
@Data
public class RequestMsg extends BaseMsg {

    /**
     * 发送时间
     */
    private long sendTime;

    /**
     * this object is msg body
     */
    private Object consumerBean;

    /**
     * 目标地址
     */
    private String targetAddress;

}
