package com.bean;

import com.yelanyanyu.annotation.Component;
import jakarta.annotation.PreDestroy;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
public class Class02 {
    @PreDestroy
    public void destroy() {
        System.out.println(this);
    }
}
