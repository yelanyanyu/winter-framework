package com.yelanyanyu.context;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.yelanyanyu.annotation.*;
import com.yelanyanyu.exception.BeanDefinitionException;
import com.yelanyanyu.exception.NoUniqueBeanDefinitionException;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.io.ResourceResolver;
import com.yelanyanyu.util.ClassPathUtils;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class AnnotationConfigApplicationContext {
    Map<String, BeanDefinition> beans;

    public Map<String, BeanDefinition> getBeans() {
        return beans;
    }

    /**
     * @param configClass      配置类的class对象，里面有ComponentScan注解
     * @param propertyResolver 静态配置集合
     */
    public AnnotationConfigApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
        Set<String> strings = scanForClassNames(configClass);
        this.beans = createBeanDefinitions(strings);
    }

    /**
     * find all classes from the given package of @ComponentScan
     *
     * @param config class where has the @Component
     * @return name set of all the classes
     */
    public Set<String> scanForClassNames(Class<?> config) {
        Set<String> res = new ConcurrentHashSet<>(16);
        ComponentScan scan = ClassPathUtils.findAnnotation(config, ComponentScan.class);
        String[] scanPackage = scan == null || scan.value().length == 0 ? new String[]{config.getPackage().getName()} : scan.value();
        for (String pkg : scanPackage) {
            ResourceResolver rr = new ResourceResolver(pkg);
            List<String> classNames = rr.scan(resource -> {
                String name = resource.name();
                if (name.endsWith(".class")) {
                    return name.substring(0, name.length() - 6).
                            replace("/", ".").replace("\\", ".");
                }
                return null;
            });
            res.addAll(classNames);
        }
        Import impAnno = config.getAnnotation(Import.class);
        if (impAnno != null) {
            for (Class<?> clazz : impAnno.value()) {
                res.add(clazz.getName());
            }
        }
        return res;
    }

    Map<String, BeanDefinition> createBeanDefinitions(Set<String> classNames) {
        ConcurrentHashMap<String, BeanDefinition> defs = new ConcurrentHashMap<>();
        //扫描所有以类的形式呈现的bean
        for (String className : classNames) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Component component = ClassPathUtils.findAnnotation(clazz, Component.class);
            if (component != null) { //要注入map
                String beanName = ClassPathUtils.getBeanName(clazz);
                BeanDefinition def = new BeanDefinition(beanName, clazz,
                        getSuitableConstructor(clazz),
                        getOrder(clazz),
                        clazz.isAnnotationPresent(Primary.class),
                        null, null, ClassPathUtils.findAnnotationMethod(PostConstruct.class, clazz),
                        ClassPathUtils.findAnnotationMethod(PreDestroy.class, clazz));

                addBeanDefinition(defs, def);
                Configuration configuration = ClassPathUtils.findAnnotation(clazz, Configuration.class);
                //若类中还有@Bean注解，那么就要以工厂模式注入
                if (configuration != null) {
                    scanFactoryMethods(beanName, clazz, defs);
                }

            }
        }
        return defs;
    }

    /**
     * 找到合适的构造器, 一个合适的构造器只能有一个
     * @param clazz bean class
     * @return constructor
     */
    Constructor<?> getSuitableConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            Constructor<?>[] dc = clazz.getDeclaredConstructors();
            if (dc.length != 1) {
                throw new BeanDefinitionException("only one constructor allowed in bean " + ClassPathUtils.getBeanName(clazz));
            }
        }
        if (constructors.length != 1) {
            throw new BeanDefinitionException("only one public constructor allowed in bean " + ClassPathUtils.getBeanName(clazz));
        }
        return constructors[0];
    }

    int getOrder(Class<?> clazz) {
        Order order = clazz.getAnnotation(Order.class);
        int value = Integer.MAX_VALUE;
        if (order != null) {
            value = order.value();
        }
        return value;
    }

    int getOrder(Method method) {
        Order order = method.getAnnotation(Order.class);
        int value = Integer.MAX_VALUE;
        if (order != null) {
            value = order.value();
        }
        return value;
    }

    /**
     * put the bean with annotation @Bean into def map
     * @param beanNameFactory name of bean with @Configuration and has @Bean method
     * @param clazz
     * @param defs
     */
    void scanFactoryMethods(String beanNameFactory, Class<?> clazz, Map<String, BeanDefinition> defs) {
        for (Method method : clazz.getDeclaredMethods()) {
            Bean bean = method.getAnnotation(Bean.class);
            if (bean != null) {
                Class<?> returnType = method.getReturnType();
                BeanDefinition def = new BeanDefinition(ClassPathUtils.getBeanName(method), returnType,
                        getSuitableConstructor(clazz),
                        getOrder(clazz),
                        clazz.isAnnotationPresent(Primary.class),
                        bean.initMethod().isEmpty() ? null : bean.initMethod(),
                        bean.destroyMethod().isEmpty() ? null : bean.destroyMethod(),
                        null, null,
                        method, beanNameFactory);
                addBeanDefinition(defs, def);
            }
        }
    }

    /**
     * add bean def into beans, multiple beans are not allowed
     * @param defs beans map
     * @param def bean def
     */
    void addBeanDefinition(Map<String, BeanDefinition> defs, BeanDefinition def) {
        //判断bean是否重复
        if (defs.put(def.getName(), def) != null)
            throw new NoUniqueBeanDefinitionException(String.format("bean %s should be unique", def.getName()));
    }


    /**
     * 得到所有类型为type的bean信息，包括声明类型为type的bean
     * @param type ABC.class
     * @return list
     */
    List<BeanDefinition> findBeanDefinitions(Class<?> type) {
        return this.beans.values().stream().filter(def -> type.isAssignableFrom(def.getBeanClass())).sorted().collect(Collectors.toList());
    }

    /**
     * 通过 get(ABC.class)的方法得到唯一的单例bean
     * @param type ABC.class
     * @return beandef
     */
    @Nullable
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> defs = findBeanDefinitions(type);
        if (defs == null) {
            return null;
        }
        if (defs.size() == 1) {
            return defs.get(0);
        }
        //如果找到的对象不唯一就遍历找出有primary注释的bean
        List<BeanDefinition> collect = defs.stream().filter(BeanDefinition::isPrimary).toList();
        if (collect.size() == 1) {
            return collect.get(0);
        }
        if (collect.isEmpty()) {
            throw new NoUniqueBeanDefinitionException(String.format("有重复的被@Primary注释的bean, 名字为: %s", type.getName()));
        } else {
            throw new NoUniqueBeanDefinitionException(String.format("有重复的被@Primary注释的bean, 名字为: %s", type.getName()));
        }
    }
}
