package com.lyw.controller;

import com.lyw.spring.ApplicationContext;
import com.lyw.spring.Component;

@Component("lyw-test")
public class Test {

    public static void main(String[] args) {
        ApplicationContext ac = new ApplicationContext(AppConfig.class);
        A bean = (A) ac.getBean(A.class);
    }
}
