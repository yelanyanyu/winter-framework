package com.yelanyanyu.aop.before;

import com.yelanyanyu.annotation.ComponentScan;
import com.yelanyanyu.annotation.Configuration;
import com.yelanyanyu.annotation.Import;
import com.yelanyanyu.aop.AfterProxyBeanPostProcessor;
import com.yelanyanyu.aop.AroundProxyBeanPostProcessor;
import com.yelanyanyu.aop.BeforeProxyBeanPostProcessor;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
@ComponentScan
@Import({BeforeProxyBeanPostProcessor.class, AfterProxyBeanPostProcessor.class, AroundProxyBeanPostProcessor.class})
public class BeforeApplication {
}
