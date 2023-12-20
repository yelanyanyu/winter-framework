package com.yelanyanyu.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public abstract class BeforeInvocationHandlerAdapter implements InvocationHandler {
    /**
     * User must implement this method, so that make @Before work.
     *
     * @param proxy  .
     * @param method .
     * @param args   .
     */
    public abstract void before(Object proxy, Method method, Object[] args);

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(proxy, method, args);
        return method.invoke(proxy, args);
    }
}
