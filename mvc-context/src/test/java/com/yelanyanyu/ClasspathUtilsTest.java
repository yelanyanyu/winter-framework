package com.yelanyanyu;

import com.bean.Class01;
import com.yelanyanyu.annotation.ComponentScan;
import com.yelanyanyu.util.ClassPathUtils;
import com.yelanyanyu.util.YamlUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ClasspathUtilsTest {
    final Logger logger = LoggerFactory.getLogger(ClasspathUtilsTest.class);

    @Test
    public void t1() {
        ComponentScan annotation = ClassPathUtils.findAnnotation(Class01.class, ComponentScan.class);
        System.out.println(annotation);
    }

    @Test
    public void t2() {
        Map<String, Object> map = YamlUtils.loadYamlMapAsPlainMap("test.yml");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            logger.info("key: {}, value: {}", key, value);
        }
    }
}
