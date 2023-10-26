package com.yelanyanyu;

import com.yelanyanyu.io.ResourceResolver;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ResourceResolverTest {
    @Test
    public void t1() {
        ResourceResolver rr = new ResourceResolver("com.yelanyanyu");
        rr.scan(resource -> {
            String name = resource.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).
                        replace("/", ".").replace("\\", ".");
            }
            return null;
        });
    }

    @Test
    public void t2() throws Exception {

    }
}
