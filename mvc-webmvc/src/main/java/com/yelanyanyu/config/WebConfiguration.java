package com.yelanyanyu.config;

import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.annotation.Configuration;
import com.yelanyanyu.webmvc.view.InternalResourceViewResolver;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
public class WebConfiguration {
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/views/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }
}
