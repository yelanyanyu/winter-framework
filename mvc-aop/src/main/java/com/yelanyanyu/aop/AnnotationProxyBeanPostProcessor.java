package com.yelanyanyu.aop;

import com.yelanyanyu.aop.exception.AopException;
import com.yelanyanyu.context.ApplicationUtils;
import com.yelanyanyu.context.BeanDefinition;
import com.yelanyanyu.context.BeanPostProcessor;
import com.yelanyanyu.context.ConfigurableApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for annotation proxy bean post processor. User can extend this class to implement their own annotation proxy bean post processor. This class is used to extend other annotation support for AOP, such as @Transactional in mvc-jdbc.
 * For example, if the user want to implement other AOP's annotation named @Transactional, they just need to extend this class and assign A as @Transactional.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public abstract class AnnotationProxyBeanPostProcessor<A extends Annotation> implements BeanPostProcessor {
    Map<String, Object> originBeans = new HashMap<>();
    Class<A> annoClass;

    public AnnotationProxyBeanPostProcessor() {
        this.annoClass = getParameterizedType();
    }

    /**
     * Retrieve the generic parameter type of this class.
     * Optional: Only one generic parameter type is allowed in this class, such as Map<K, V> is not allowed.
     *
     * @return .
     */
    @SuppressWarnings("unchecked")
    Class<A> getParameterizedType() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            log.error("Can not get the generic parameter type of class: {}", getClass().getSimpleName());
            throw new AopException(String.format("Can not get the generic parameter type of class: %s", getClass().getSimpleName()));
        }

        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
        if (actualTypeArguments.length != 1) {
            log.error("There is only one generic parameter type in class: {}", getClass().getSimpleName());
            throw new IllegalArgumentException(String.format("There is only one generic parameter type in class: %s", getClass().getSimpleName()));
        }

        if (!(actualTypeArguments[0] instanceof Class)) {
            log.error("The generic parameter type of class: {} is not a class", getClass().getSimpleName());
            throw new IllegalArgumentException(String.format("The generic parameter type of class: %s is not a class", getClass().getSimpleName()));
        }
        return (Class<A>) actualTypeArguments[0];
    }

    /**
     * If the bean is annotated with AOP's annotation, replace the origin bean with a proxy bean.
     *
     * @param bean     .
     * @param beanName .
     * @return .
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        A anno = beanClass.getAnnotation(annoClass);
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

        // If the bean isn't annotated with AOP's annotation, return the origin bean.
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
