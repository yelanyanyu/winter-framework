package com.yelanyanyu.aop.around;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.After;
import lombok.NoArgsConstructor;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@After("afterHandler")
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
