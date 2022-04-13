package com.lyw.spring;

import lombok.Data;

/**
 * Bean 的定义信息
 */
@Data
public class BeanDefinition {

    // bean名称
    private String beanName;

    // 类的类型
    private Class type;

    // 作用域
    private String scope;
}
