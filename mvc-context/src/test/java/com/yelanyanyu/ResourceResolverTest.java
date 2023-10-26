package com.yelanyanyu;

import com.yelanyanyu.io.ResourceResolver;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ResourceResolverTest {
    @Test
    public void t1() {
        ResourceResolver rr = new ResourceResolver("cn.hutool");
        rr.scan(resource -> {
            String name = resource.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).
                        replace("/", ".").replace("\\", ".");
            }
            return null;
        }).forEach(System.out::println);
    }

    @Test
    public void t2() throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("cn/hutool");
        Files.walk(jarUriToPath("cn/hutool", resource.toURI())).forEach(System.out::println);
    }

    private Path jarUriToPath(String basePackagePath, URI jarUri) {
        try {
            return FileSystems.newFileSystem(jarUri, Map.of()).getPath(basePackagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
