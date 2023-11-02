package com.yelanyanyu.context;

import com.yelanyanyu.annotation.Bean;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface ConfigurableApplicationContext extends ApplicationContext {
    List<BeanDefinition> findBeanDefinitions(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(Class<?> type);

    @Nullable
    BeanDefinition findBeanDefinition(String name);

    @Nullable
    BeanDefinition findBeanDefinition(String name, Class<?> type);

    Object createBeanAsEarlySingleton(BeanDefinition def);
}
