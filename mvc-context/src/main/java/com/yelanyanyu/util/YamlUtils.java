package com.yelanyanyu.util;

import org.yaml.snakeyaml.Yaml;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class YamlUtils {
    /**
     * Transform
     *
     * @param path
     * @return
     */
    public static Map<String, Object> loadYamlMapAsPlainMap(String path) {
        Yaml yaml = new Yaml();
        Map<String, Object> map = ClassPathUtils.readInputStream(path, (input) -> {
            return (Map<String, Object>) yaml.load(input);
        });
        Map<String, Object> ans = new HashMap<>();
        convertTo(map, "", ans);
        return ans;
    }


    /**
     * Transforming a map containing list and map types into string-to-string map, like the res of properties.load()
     *
     * @param yamlMap string-to-object map
     */
    static void convertTo(Map<String, Object> yamlMap, String prefix,
                          Map<String, Object> plainMap) {
        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = entry.getKey();
            Object v = entry.getValue();
            if (v instanceof Map<?, ?>) {
                Map<String, Object> next = (Map<String, Object>) v;
                convertTo(next, prefix + key + ".", plainMap);
            } else if (v instanceof List<?>) {
                plainMap.put(prefix + key, v);
            } else {
                plainMap.put(prefix + key, v);
            }
        }
    }
}
