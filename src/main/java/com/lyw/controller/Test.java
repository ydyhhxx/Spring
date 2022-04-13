package com.lyw.controller;

import com.lyw.spring.*;

@Component("lyw-test")
public class Test implements BeanNameAware {

    @AutoWired
    private A a;

    private String beanName;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ApplicationContext(AppConfig.class);
        A bean = (A) ac.getBean(A.class);
    }
}
