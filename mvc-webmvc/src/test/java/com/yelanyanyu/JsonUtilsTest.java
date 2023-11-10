package com.yelanyanyu;

import com.yelanyanyu.bean.TestBean;
import com.yelanyanyu.webmvc.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class JsonUtilsTest {
    @Test
    public void t1() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(Thread.currentThread()
                        .getContextClassLoader().
                        getResourceAsStream("test.json"))));
        Object o = JsonUtils.readerToBean(bufferedReader, TestBean.class);
        log.debug("obj: {}", o);
        log.debug("str: {}", JsonUtils.writeToString(o));
        JsonUtils.writeJson(new BufferedWriter(new OutputStreamWriter(System.out)), o);
    }
}
