package com.lanmo.canary.client.transport;

import com.lanmo.canary.register.Provider;

import lombok.Data;

/**
 * Created by bowen on 2017/1/22.
 */
@Data
public class Connection {

    private Provider provider;

    private ClientTransport clientTransport;

}
