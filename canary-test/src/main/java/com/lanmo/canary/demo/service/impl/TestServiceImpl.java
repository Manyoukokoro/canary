package com.lanmo.canary.demo.service.impl;

import com.lanmo.canary.demo.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2017/3/7.
 */
public class TestServiceImpl implements TestService {
    Logger logger= LoggerFactory.getLogger(this.getClass());
    public String sayHello(String say, Integer sayNum) {

        for(int i=0;i<sayNum;i++){
            logger.info("say "+i+" >>>>>"+say);
        }

        return "server>>>>"+say;
    }
}
