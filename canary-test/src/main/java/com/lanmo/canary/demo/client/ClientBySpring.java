package com.lanmo.canary.demo.client;

import com.lanmo.canary.demo.service.TestService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by bowen on 2017/2/7.
 */
public class ClientBySpring {

    public static void main(String[] args){

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");

        TestService testService = (TestService) context.getBean("testService");

        String str=testService.sayHello("王波",20);

        System.out.print(str);

    }

}
