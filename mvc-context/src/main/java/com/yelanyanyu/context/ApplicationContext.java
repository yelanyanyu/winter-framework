package com.yelanyanyu.context;

import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public interface ApplicationContext extends AutoCloseable {
    boolean containsBean(String name);

    <T> T getBean(String name);

    <T> T getBean(Class<T> type);

    <T> T getBean(String name, Class<T> type);

    <T> List<T> getBeans(Class<T> type);

    void close();
}
