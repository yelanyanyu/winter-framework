package com.yelanyanyu.aop;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.Around;

/**
 * Replace the origin bean with a proxy bean.
 *
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
public class AroundProxyBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Around> {
}
