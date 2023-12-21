package com.yelanyanyu;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class PathUtilsTest {
    @Test
    public void t1() {
        String path = "/product/{productId}/detail/{detailId}";
        String regPath = path.replaceAll("\\{([a-zA-Z][a-zA-Z0-9]*)\\}", "(?<$1>[^/]*)");
        if (regPath.indexOf('{') >= 0 || regPath.indexOf('}') >= 0) {
            log.error("failed");
        }
        log.info("res: {}", regPath);
        Pattern pattern = Pattern.compile("^" + regPath + "$");
        log.info("pattern: {}", pattern);
        Matcher matcher = pattern.matcher("/product/1/detail/2");
        matcher.find();
        log.info("res: {}", matcher.group("productId"));
    }
}
