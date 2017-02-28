package com.lanmo.canary.netty.message;

import lombok.Data;

/**
 * Created by bowen on 2017/1/17.
 * 封装response消息体
 */
@Data
public class ResponseMsg extends BaseMsg {

    private long receiveTime;

    /**
     * this object is msg body
     */
    private Object response;

}
