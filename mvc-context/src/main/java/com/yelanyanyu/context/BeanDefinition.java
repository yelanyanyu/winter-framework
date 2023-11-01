package com.yelanyanyu.context;

import lombok.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Data
public class BeanDefinition implements Comparable<BeanDefinition> {
    /**
     * bean实例的名称
     */
    private final String name;
    /**
     * bean 的class对象
     */
    private final Class<?> beanClass;
    /**
     * bean 对象实例
     */
    private Object instance = null;
    /**
     * bean 构造器
     */
    private final Constructor<?> constructor;
    /**
     * 工厂方法名称
     */
    private final String factoryName;
    /**
     * 工厂方法对象
     */
    private final Method factoryMethod;
    /**
     * bean 的顺序
     */
    private final int order;
    /**
     * 是否被 @Primary 注释
     */
    private final boolean primary;
    /**
     * init and destroy
     */
    private boolean init = false;
    private String initMethodName;
    private String destroyMethodName;
    private Method initMethod;
    private Method destoyMethod;

    /**
     * 若没有被@Bean注释，则使用这个构造器
     */
    public BeanDefinition(String name, Class<?> beanClass, Constructor<?> constructor, int order, boolean primary, String initMethodName,
                          String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        constructor.setAccessible(true);
        this.constructor = constructor;
        this.order = order;
        this.primary = primary;
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
        this.factoryName = null;
        this.factoryMethod = null;
        setInitAndDestroyMethod(initMethodName, destroyMethodName, initMethod, destroyMethod);
    }

    /**
     * 被@Bean注释的bean使用的构造器
     */
    public BeanDefinition(String name, Class<?> beanClass, Constructor<?> constructor, int order,
                          boolean primary, String initMethodName,
                          String destroyMethodName, Method initMethod, Method destroyMethod,
                          Method factoryMethod, String factoryName) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = null;
        this.order = order;
        this.primary = primary;
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
        setInitAndDestroyMethod(initMethodName, destroyMethodName, initMethod, destroyMethod);
        this.factoryName = factoryName;
        factoryMethod.setAccessible(true);
        this.factoryMethod = factoryMethod;
    }

    private void setInitAndDestroyMethod(String initMethodName, String destroyMethodName, Method initMethod, Method destoyMethod) {
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
        if (initMethod != null) {
            initMethod.setAccessible(true);
        }
        if (destoyMethod != null) {
            destoyMethod.setAccessible(true);
        }
        this.initMethod = initMethod;
        this.destoyMethod = destoyMethod;
    }

    /**
     * 优先比较order，再比较name的字典序
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(BeanDefinition o) {
        int cmp = Integer.compare(this.order, o.order);
        if (cmp != 0) {
            return cmp;
        }
        return this.name.compareTo(o.name);
    }
}
