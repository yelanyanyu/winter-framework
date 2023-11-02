package com.bean;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class Class02Proxy extends Class02 {
    final Class02 target;

    public Class02Proxy(Class02 class02) {
        this.target = class02;
    }
}
