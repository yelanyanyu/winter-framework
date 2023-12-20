package com.yelanyanyu.aop.before;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.BeforeInvocationHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
@Slf4j
public class BeforeHandler extends BeforeInvocationHandlerAdapter {
    @Override
    public void before(Object proxy, Method method, Object[] args) {
        log.info("BeforeHandler: before method: {}", method.getName());
    }
}
