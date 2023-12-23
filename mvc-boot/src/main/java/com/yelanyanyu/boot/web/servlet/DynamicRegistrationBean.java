package com.yelanyanyu.boot.web.servlet;

import jakarta.servlet.Registration;

import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class DynamicRegistrationBean<D extends Registration.Dynamic> {
    private String name;
    private Map<String, String> initParams;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public void setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
    }
}
