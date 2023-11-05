package com.yelanyanyu;

import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.web.util.WebUtils;
import org.junit.Test;

import java.lang.annotation.Target;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class WebUtilsTest {
    @Test
    public void t1() {
        PropertyResolver propertyResolver = WebUtils.createPropertyResolver();
    }
}
