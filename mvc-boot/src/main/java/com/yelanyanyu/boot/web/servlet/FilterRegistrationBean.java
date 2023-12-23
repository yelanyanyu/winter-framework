package com.yelanyanyu.boot.web.servlet;

import jakarta.servlet.Filter;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class FilterRegistrationBean<T extends Filter> extends AbstractFilterRegistrationBean<T> {
    T filter;

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }
}
