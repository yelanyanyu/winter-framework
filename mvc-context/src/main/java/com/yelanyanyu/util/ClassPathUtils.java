package com.yelanyanyu.util;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ClassPathUtils {
    static ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassPathUtils.class.getClassLoader();
        }
        return cl;
    }
}
