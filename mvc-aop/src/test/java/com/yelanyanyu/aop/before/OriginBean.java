package com.yelanyanyu.aop.before;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.Before;
import lombok.NoArgsConstructor;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@Before("beforeHandler")
@NoArgsConstructor
public class OriginBean {
    public String name;

    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
