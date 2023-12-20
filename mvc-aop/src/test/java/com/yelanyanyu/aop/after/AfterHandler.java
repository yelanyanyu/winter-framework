package com.yelanyanyu.aop.after;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.AfterInvocationHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@Slf4j
public class AfterHandler extends AfterInvocationHandlerAdapter {
    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args) {
        log.info("AfterHandler: after method: {}", method.getName());
        log.info("proxy: {}", proxy);
        log.info("res: {}", returnValue);
        return returnValue;
    }
}
