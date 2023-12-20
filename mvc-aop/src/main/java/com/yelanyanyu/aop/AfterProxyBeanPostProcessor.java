package com.yelanyanyu.aop;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.After;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
public class AfterProxyBeanPostProcessor extends AnnotationProxyBeanPostProcessor<After> {
}
