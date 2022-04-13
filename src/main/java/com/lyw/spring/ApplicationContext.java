package com.lyw.spring;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(64);

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 是否配置了包扫描路径
        ComponentScan scan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        if (scan == null)
            throw new RuntimeException("未配置@ComponentScan");
        String path = scan.value(); // com.lyw.controller
        if (StringUtils.isBlank(path))
            throw new RuntimeException("包扫描路径是空");

        path = path.replace(".", "/"); // com/lyw/controller
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        // resource=file:/Users/ywli/Desktop/mytest/workspace/Spring/target/classes/com/lyw/controller
        URL resource = classLoader.getResource(path);
        if (resource == null)
            throw new RuntimeException("路径不存在");

        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            try {
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    // 是否是 .class 文件
                    if (!fileName.endsWith(".class")) {
                        continue;
                    }
                    // className = com/lyw/controller/Test
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("/", ".");

                    Class<?> loadClass = classLoader.loadClass(className);
                    // 是否是要被加载的 Bean
                    Component component = loadClass.getAnnotation(Component.class);
                    if (component == null)
                        continue;

                    // 创建 BeanDefinition
                    BeanDefinition beanDefinition = new BeanDefinition();
                    Scope scope = loadClass.getAnnotation(Scope.class);
                    if (scope == null) {
                        beanDefinition.setScope("singleton");
                    } else {
                        String value = scope.value();
                        beanDefinition.setScope(StringUtils.isBlank(value) ? "singleton" : value);
                    }
                    String beanName = component.value();
                    beanDefinition.setType(loadClass);
                    beanDefinition.setBeanName(beanName);

                    beanDefinitionMap.put(beanName, beanDefinition);
                }
            } catch (ClassNotFoundException e) {
            }
        }
    }

    public Object getBean(String name) {
        return null;
    }

    public Object getBean(Class clazz) {
        return null;
    }
}
