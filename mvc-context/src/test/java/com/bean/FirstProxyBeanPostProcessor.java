package com.bean;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.context.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
public class FirstProxyBeanPostProcessor implements BeanPostProcessor {
    Map<String, Object> originBeans = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (Class02.class.isAssignableFrom(bean.getClass())) {
            Class02Proxy class02Proxy = new Class02Proxy((Class02) bean);
            originBeans.put(beanName, bean);
            System.out.println("postProcessor...");
            return class02Proxy;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("after...");
        return bean;
    }

    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        Object origin = originBeans.get(beanName);
        if (origin != null) {
            // return the original bean if it exists
            return origin;
        }
        return bean;
    }
}
