package com.yelanyanyu.util;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Objects;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class YamlUtils {
    public static Map<String, Object> loadYamlMapAsPlainMap(String path) {
        Yaml yaml = new Yaml();
        return yaml.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
    }
}
