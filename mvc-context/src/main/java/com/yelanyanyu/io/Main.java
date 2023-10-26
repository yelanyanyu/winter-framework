package com.yelanyanyu.io;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class Main {
    public static void main(final String[] argv) throws IOException, URISyntaxException {
        String basePackage = "cn.hutool";
        String basePackage1 = "com.yelanyanyu";
        ResourceResolver resourceResolver = new ResourceResolver(basePackage);
        List<String> scan = resourceResolver.scan(res -> {
            String name = res.name(); // 资源名称"org/example/Hello.class"
            if (name.endsWith(".class")) { // 如果以.class结尾
                // 把"org/example/Hello.class"变为"org.example.Hello":
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            // 否则返回null表示不是有效的Class Name:
            return null;
        });
        scan.forEach(System.out::println);
    }
}

