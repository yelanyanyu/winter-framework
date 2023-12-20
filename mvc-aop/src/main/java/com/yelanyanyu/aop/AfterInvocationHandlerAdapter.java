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
     * @param proxy       .
     * @param returnValue .
     * @param method      .
     * @param args        .
     */
    public abstract Object after(Object proxy, Object returnValue, Method method, Object[] args);

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(proxy, args);
        return after(proxy, result, method, args);
    }
}
