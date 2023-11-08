package com.yelanyanyu.web.util;

import com.yelanyanyu.web.exception.ServletException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class PathUtils {
    /**
     * Creating a pattern object that allows the retrieval of PathVariable's value from the URI using group name.
     *
     * @param path path
     * @return pattern
     */
    public static Pattern compile(String path) {
        String regPath = path.replaceAll("\\{([a-zA-Z][a-zA-Z0-9]*)}", "(?<$1>[^/]*)");
        if (regPath.indexOf('{') >= 0 || regPath.indexOf('}') >= 0) {
            throw new ServletException("Invalid handler path: " + path);
        }
        return Pattern.compile("^" + regPath + "$");
    }
}
