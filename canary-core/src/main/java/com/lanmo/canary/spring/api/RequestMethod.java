package com.lanmo.canary.spring.api;

import lombok.Data;

/**
 * Created by bowen on 2017/1/18.
 */
@Data
public class RequestMethod {

    private String method;

    private Object[] methodParams;

    private String[] parameterTypes;

    public void setMethodParams0(Object... methodParams) {
        this.methodParams = methodParams;
    }

}
