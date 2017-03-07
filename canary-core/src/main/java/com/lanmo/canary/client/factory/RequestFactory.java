package com.lanmo.canary.client.factory;

import com.bowen.ant.config.ConsumerConfig;
import com.bowen.ant.constant.AntConstants;
import com.bowen.ant.msg.MsgHeader;
import com.bowen.ant.msg.RequestMsg;

/**
 * Created by cdliujian1 on 2016/11/4.
 */
public class RequestFactory {

    public static RequestMsg buildRequest(ConsumerConfig consumerConfig){
        RequestMsg requestMsg = new RequestMsg();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setClz(ConsumerConfig.class.getCanonicalName());
        msgHeader.setMsgType(AntConstants.REQUEST_MSG);

        requestMsg.setMsgHeader(msgHeader);
        requestMsg.setConsumerBean(consumerConfig);
        return requestMsg;
    }

}
