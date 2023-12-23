package com.yelanyanyu.boot.web.servlet;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class AbstractFilterRegistrationBean<T extends Filter> extends DynamicRegistrationBean<FilterRegistration.Dynamic> {
    private Set<String> urlPatterns;

    public Set<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(Set<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public boolean isMatchAfter() {
        return isMatchAfter;
    }

    public void setMatchAfter(boolean matchAfter) {
        isMatchAfter = matchAfter;
    }

    public EnumSet<DispatcherType> getDispatcherTypes() {
        return dispatcherTypes;
    }

    public void setDispatcherTypes(EnumSet<DispatcherType> dispatcherTypes) {
        this.dispatcherTypes = dispatcherTypes;
    }

    private boolean isMatchAfter;
    private EnumSet<DispatcherType> dispatcherTypes;
}
