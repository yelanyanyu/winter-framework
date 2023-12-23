package com.yelanyanyu.app;

import com.yelanyanyu.annotation.*;
import com.yelanyanyu.app.filter.Filter_01;
import com.yelanyanyu.app.filter.Filter_02;
import com.yelanyanyu.boot.web.servlet.FilterRegistrationBean;
import com.yelanyanyu.jdbc.JdbcConfiguration;
import com.yelanyanyu.web.WebMvcConfiguration;
import jakarta.servlet.DispatcherType;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Configuration
@ComponentScan
@Import({JdbcConfiguration.class, WebMvcConfiguration.class})
public class HelloConfiguration {
    @Bean
    @Order(2)
    public FilterRegistrationBean<Filter_01> filterRegistrationBean_01() {
        FilterRegistrationBean<Filter_01> reg = new FilterRegistrationBean<>();
        reg.setFilter(new Filter_01());
        reg.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
        reg.setMatchAfter(true);
        reg.setUrlPatterns(Set.of("/*"));
        reg.setName("filer_01");
        return reg;
    }

    @Bean
    @Order(1)
    public FilterRegistrationBean<Filter_02> filterRegistrationBean_02() {
        FilterRegistrationBean<Filter_02> reg = new FilterRegistrationBean<>();
        reg.setFilter(new Filter_02());
        reg.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST));
        reg.setMatchAfter(true);
        reg.setUrlPatterns(Set.of("/*"));
        reg.setName("filer_02");
        return reg;
    }
}
