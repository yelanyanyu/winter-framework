package com.yelanyanyu.jdbc.transaction;

import com.yelanyanyu.annotation.Component;
import com.yelanyanyu.aop.AnnotationProxyBeanPostProcessor;
import com.yelanyanyu.jdbc.annotation.Transactional;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Component
public class TransactionBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Transactional> {
}
