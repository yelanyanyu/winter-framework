package com.yelanyanyu.util;

import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.exception.BeanCreationException;
import com.yelanyanyu.exception.BeanDefinitionException;
import com.yelanyanyu.io.InputStreamCallback;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ClassPathUtils {
    final static Logger logger = LoggerFactory.getLogger(ClassPathUtils.class);
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
                    throw new BeanDefinitionException("Duplicate @" + annotationClass.getSimpleName() + " for class " + annotatedBeanClass.getSimpleName());
                }
                annotation = found;
            }
        }
        return annotation;
    }


    public static String getBeanName(Class<?> clazz) {
        return StringUtils.uncapitalize(clazz.getSimpleName());
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
        return StringUtils.uncapitalize(name);
    }

    @Nullable
    public static Method findAnnotationMethod(Class<? extends Annotation> annoClass, Class<?> beanClass) {
        List<Method> ms = Arrays.stream(beanClass.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(annoClass)).map(m -> {
            if (m.getParameterCount() != 0) {
                throw new BeanDefinitionException(String.format("method '%s.%s' annotated with '@%s' has multiple parameters",
                        beanClass.getName(), m.getName(), annoClass.getName()));
            }
            return m;
        }).toList();
        if (ms.isEmpty()) {
            return null;
        }
        if (ms.size() != 1) {
            throw new BeanDefinitionException(String.format("multiple method annotated with '@%s'",
                    annoClass.getName()));
        }
        return ms.get(0);
    }

    /**
     * Get non-arg method by method name
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static Method findMethodByName(Class<?> clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readInputStream(String path, InputStreamCallback<T> inputStreamCallback) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream input = getContextClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new FileNotFoundException("File not found in classpath: " + path);
            }
            return inputStreamCallback.apply(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
