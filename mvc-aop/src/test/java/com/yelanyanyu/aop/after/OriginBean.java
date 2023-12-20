package com.yelanyanyu.aop.after;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.Around;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@Around("afterHandler")
//@Before("beforeHandler")
public class OriginBean {
    public String name;

    public void hello() {
        System.out.println("Hello, " + name + ".");
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
