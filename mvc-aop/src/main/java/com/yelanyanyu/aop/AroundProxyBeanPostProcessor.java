package com.yelanyanyu.aop;

import com.yelanyanyu.aop.annotation.Around;
import com.yelanyanyu.aop.exception.AopException;
import com.yelanyanyu.context.ApplicationUtils;
import com.yelanyanyu.context.BeanDefinition;
import com.yelanyanyu.context.BeanPostProcessor;
import com.yelanyanyu.context.ConfigurableApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Replace the origin bean with a proxy bean.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class AroundProxyBeanPostProcessor implements BeanPostProcessor {
    Map<String, Object> originBeans = new HashMap<>();

    /**
     * If the bean is annotated with @Around, replace the origin bean with a proxy bean.
     *
     * @param bean     .
     * @param beanName .
     * @return .
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        Around anno = beanClass.getAnnotation(Around.class);
        if (anno != null) {
            String handlerName;
            try {
                handlerName = (String) anno.annotationType().getMethod("value").invoke(anno);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error("Exception: {}", e.getMessage());
                throw new RuntimeException(e);
            }
            Object proxyBean = createProxyBean(beanClass, bean, handlerName);
            this.originBeans.put(beanName, bean);
            return proxyBean;
        }

        // If the bean isn't annotated with @Around, return the origin bean.
        return bean;
    }

    Object createProxyBean(Class<?> beanClass, Object bean, String handlerName) {
        // Get the instance of ApplicationContext.
        ConfigurableApplicationContext ioc = (ConfigurableApplicationContext) ApplicationUtils.getRequiredApplicationContext();
        // Get the bean definition of the handler.
        BeanDefinition def = ioc.findBeanDefinition(handlerName);
        if (def == null) {
            log.error("Can not find bean definition for aop handler: {} when create proxy bean: {}", handlerName, beanClass.getSimpleName());
            throw new AopException(String.format("Can not find bean definition for aop handler: %s", handlerName));
        }

        Object handlerBean = def.getInstance();
        if (handlerBean == null) {
            // If bean has not been created, create it.
            handlerBean = ioc.createBeanAsEarlySingleton(def);
        }

        if (handlerBean instanceof InvocationHandler handler) {
            return ProxyResolver.INSTANCE.createProxy(bean, handler);
        }

        log.error("Bean: {} annotated with @Around, but bean: {} annotated with @AroundHandler is not an instance of InvocationHandler", beanClass.getSimpleName(), handlerName);
        throw new AopException(String.format("Bean: %s annotated with @Around, but bean: %s annotated with @AroundHandler is not an instance of InvocationHandler", beanClass.getSimpleName(), handlerName));
    }

    /**
     * Replace the proxy bean with the origin bean.
     *
     * @param bean     .
     * @param beanName .
     * @return .
     */
    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        Object origin = this.originBeans.get(beanName);
        return origin == null ? bean : origin;
    }
}
