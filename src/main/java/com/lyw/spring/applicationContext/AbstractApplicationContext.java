package com.lyw.spring.applicationContext;

import com.lyw.spring.BeanException;

public class AbstractApplicationContext implements ConfigurableApplicationContext {

    @Override
    public Object getBean(String beanName) throws BeanException {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> requireType) throws BeanException {
        return null;
    }

    @Override
    public boolean containsBean(String name) {
        return false;
    }
}
