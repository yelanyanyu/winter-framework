package com.yelanyanyu.aop.after;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.After;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@After("afterHandler")
//@Before("beforeHandler")
public class OriginBean {
    public String name;

    public String hello() {
        System.out.println("Hello, " + name + ".");
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
