package com.yelanyanyu.webmvc;

import com.yelanyanyu.annotation.Bean;
import com.yelanyanyu.annotation.Configuration;

import javax.servlet.ServletContext;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
public class WebMvcConfiguration {
    static ServletContext context;

    static void setServletContext(ServletContext ctx) {
        context = ctx;
    }

    @Bean
    public ServletContext servletContext() {
        return context;
    }
}
