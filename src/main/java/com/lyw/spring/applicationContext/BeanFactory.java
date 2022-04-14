package com.lyw.spring.applicationContext;

import com.lyw.spring.BeanException;

public interface BeanFactory {

    String FACTORY_BEAN_PREFIX = "&";

    Object getBean(String beanName) throws BeanException;

    <T> T getBean(Class<T> requireType) throws BeanException;

    boolean containsBean(String name);
}
