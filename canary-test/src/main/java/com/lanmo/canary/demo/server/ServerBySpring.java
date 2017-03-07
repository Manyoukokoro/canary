package com.lanmo.canary.demo.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2017/2/19.
 */
public class ServerBySpring {


    public static void main(String[] args){

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContextPP.xml");

    }

}
