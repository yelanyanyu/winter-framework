package com.yelanyanyu.aop.bean;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.Around;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@Around("aroundInvocationHandler")
public class OriginBean {
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
