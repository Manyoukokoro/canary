package com.lanmo.canary.client.factory;

import com.lanmo.canary.common.constant.CanaryConstants;
import com.lanmo.canary.netty.message.MsgHeader;
import com.lanmo.canary.netty.message.RequestMsg;
import com.lanmo.canary.spring.api.ReferenceConfig;

/**
 * Created by cdliujian1 on 2016/11/4.
 */
public class RequestFactory {

    public static RequestMsg buildRequest(ReferenceConfig referenceConfig){
        RequestMsg requestMsg = new RequestMsg();
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setClz(ReferenceConfig.class.getCanonicalName());
        msgHeader.setMsgType(CanaryConstants.REQUEST_MSG);

        requestMsg.setMsgHeader(msgHeader);
        requestMsg.setConsumerBean(referenceConfig);
        return requestMsg;
    }

}
