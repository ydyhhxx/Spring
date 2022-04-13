package com.lyw.spring;

import org.apache.commons.lang3.StringUtils;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Class configClass;

    // 存放 beanDefinition
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(64);
    // 单例池
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap(64);

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 是否配置了包扫描路径
        ComponentScan scan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        if (scan == null)
            throw new NullPointerException("未配置@ComponentScan");
        String path = scan.value(); // com.lyw.controller
        if (StringUtils.isBlank(path))
            throw new NullPointerException("包扫描路径是空");

        path = path.replace(".", "/"); // com/lyw/controller
        ClassLoader classLoader = ApplicationContext.class.getClassLoader();
        // resource=file:/Users/ywli/Desktop/mytest/workspace/Spring/target/classes/com/lyw/controller
        URL resource = classLoader.getResource(path);
        if (resource == null)
            throw new NullPointerException("路径不存在");

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
                    if ("".equals(beanName))
                        beanName = Introspector.decapitalize(loadClass.getSimpleName());

                    beanDefinition.setType(loadClass);
                    beanDefinition.setBeanName(beanName);

                    beanDefinitionMap.put(beanName, beanDefinition);

                    String finalBeanName = beanName;
                    beanDefinitionMap.forEach((k, v) -> {
                        Object bean = createBean(finalBeanName, beanDefinition);
                        singletonObjects.put(finalBeanName, bean);
                    });
                }
            } catch (ClassNotFoundException e) {
            }
        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object instance;
        try {
            instance = clazz.getConstructor().newInstance();

            // 依赖注入
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(AutoWired.class)) {
                    f.setAccessible(true);
                    f.set(clazz, getBean(f.getName()));
                }
            }

            // BeanNameAware 回调
            if (instance instanceof BeanNameAware)
                ((BeanNameAware) instance).setBeanName(beanName);

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null)
            throw new NullPointerException("beanDefinition不存在");

        if (!"singleton".equals(beanDefinition.getScope()))
            return createBean(beanName, beanDefinition);

        Object obj = singletonObjects.get(beanName);
        if (obj == null) {
            obj = createBean(beanName, beanDefinition);
            singletonObjects.put(beanName, obj);
        }
        return obj;
    }

    public Object getBean(Class clazz) {
        return null;
    }
}
