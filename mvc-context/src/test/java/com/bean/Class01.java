package com.bean;

import com.yelanyanyu.annotation.*;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Import(class02.class)
@Configuration
public class Class01 {
    @Bean
    public class03 class03(@Autowired Class04 class04, @Value("${a.b.c:100}")String name) {
        System.out.println("===================test-bean: " + class04);
        System.out.println("===================test-value: " + name);
        return new class03();
    }
}
