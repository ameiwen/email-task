package com.bl;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {

    public static void main(String[] agrs){
        //启动spring容器
        new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        synchronized (MainTest.class) {
            try {
                MainTest.class.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
