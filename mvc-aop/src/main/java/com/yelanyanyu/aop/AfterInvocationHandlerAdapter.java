package com.yelanyanyu.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class AfterInvocationHandlerAdapter implements InvocationHandler {
    /**
     * User must implement this method, so that make @After work.
     *
     * @param proxy  .
     * @param method .
     * @param args   .
     */
    public abstract void after(Object proxy, Method method, Object[] args);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        after(proxy, method, args);
        return method.invoke(proxy, args);
    }
}
