package com.lyw.controller;

import com.lyw.spring.*;
import com.lyw.spring.applicationContext.AnnotationConfigApplicationContext;
import com.lyw.spring.applicationContext.ApplicationContext;

@Component("lyw-test")
public class Test implements BeanNameAware {

//    @AutoWired
//    private A a;

    private String beanName;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public static void main(String[] args) {
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        A bean = (A) ac.getBean("a");
    }
}
