package com.yelanyanyu.util;

import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.exception.BeanDefinitionException;
import jakarta.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ClassPathUtils {
    static ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassPathUtils.class.getClassLoader();
        }
        return cl;
    }

    /**
     * 递归获取注解
     *
     * @param annotatedBeanClass 待扫描的bean
     * @param annotationClass
     * @param <A>
     * @return
     */
    public static <A extends Annotation> A findAnnotation(Class<?> annotatedBeanClass, Class<A> annotationClass) {
        A annotation = annotatedBeanClass.getAnnotation(annotationClass);
        for (Annotation anno : annotatedBeanClass.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (annoType.getPackageName().equals("java.lang.annotation")) {
                continue;
            }
            A found = findAnnotation(annoType, annotationClass);
            if (found != null) {
                if (annotation != null) {
                    throw new BeanDefinitionException("Duplicate @" + annotationClass.getSimpleName() + "for class " + annotationClass.getSimpleName());
                }
                annotation = found;
            }
        }
        return annotation;
    }

    public static String getBeanName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    public static String getBeanName(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        String name = null;
        if (bean != null) {
            name = bean.value();
            if (name.isEmpty()) {
                name = method.getName();
            }
        }
        return name;
    }

    @Nullable
    public static Method findAnnotationMethod(Class<? extends Annotation> annoClass, Class<?> beanClass) {
        return null;
    }
}
