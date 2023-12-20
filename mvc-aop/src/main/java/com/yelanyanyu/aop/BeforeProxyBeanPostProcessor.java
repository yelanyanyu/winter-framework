package com.yelanyanyu.aop;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.annotation.Before;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
public class BeforeProxyBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Before> {
}
