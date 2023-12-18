package com.yelanyanyu;

import cn.hutool.core.util.StrUtil;
import org.junit.Test;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class StrUtilTest {
    @Test
    public void t1() {
        String str = "aBC_abc";
        System.out.println(StrUtil.toUnderlineCase(str));
        System.out.println(StrUtil.toCamelCase(str));
    }
}
