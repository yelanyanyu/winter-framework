package com.yelanyanyu;

import com.yelanyanyu.annotation.ComponentScan;
import com.yelanyanyu.annotation.Configuration;
import com.yelanyanyu.annotation.Import;
import com.yelanyanyu.annotation.Order;
import com.yelanyanyu.webmvc.WebMvcConfiguration;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@ComponentScan
@Configuration
@Import(WebMvcConfiguration.class)
@Order(1)
public class WebConfig {

}
