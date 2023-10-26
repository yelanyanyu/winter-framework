package com.yelanyanyu.io;

import cn.hutool.core.lang.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ResourceResolver {
    String basePackage;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 扫描basePackage下的所有文件，并将其存入 list 中
     *
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> List<R> scan(Function<Resource, R> mapper) {
        String basePackagePath = this.basePackage.replace(".", "/");
        List<R> res = new ArrayList<>();
        scan0(res, this.basePackage, basePackagePath, mapper);
        return res;
    }

    /**
     * @param resources
     * @param basePackage     例如， com.yelanyanyu
     * @param basePackagePath 将上面的字符改为 com/yelanyanyu
     * @param mapper 函数式接口实现
     * @param <R>
     */
    private <R> void scan0(List<R> resources, String basePackage, String basePackagePath, Function<Resource, R> mapper) {
        ClassLoader cl = getContextClassLoader();
        URL resource = cl.getResource(basePackagePath);
        try {
            Assert.notNull(resource);
            URI uri = resource.toURI();
            //base: 类路径
            String uriBaseDir = removeTrailingSlash(uriToString(uri));
            String base = uriBaseDir.substring(0, uriBaseDir.length() - basePackage.length());
            if (uri.toString().startsWith("file:/")) {
                base = removeLeadingSlash(base.substring(5).replace("/", File.separator));
            }
            if (uri.toString().startsWith("file:/")) {
                scanPackage(false, resources, Paths.get(uri), base, mapper);
            } else if (uri.toString().startsWith("jar:")) {
                scanPackage(true, resources, jarUriToPath(basePackagePath, uri), base, mapper);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param isJar true: 是 Jar类型的资源
     * @param resource 结果集
     * @param root 需要从哪个路径下开始扫描
     * @param base 类路径：..\classes\
     * @param mapper 函数式接口实现
     * @param <R>
     */
    private <R> void scanPackage(boolean isJar, List<R> resource, Path root, String base, Function<Resource, R> mapper) {
        try {
            Files.walk(root).filter(Files::isRegularFile).forEach(fileOfPath -> {
                Resource res = null;
                if (isJar) {
                    String name = removeTrailingSlash(fileOfPath.toString());
                    res = new Resource(base + name, name);
                } else {
                    res = new Resource("file:" + base, removeLeadingSlash(fileOfPath.toString().substring(base.length())));
                }
                R apply = mapper.apply(res);
                if (apply != null) {
                    resource.add(apply);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path jarUriToPath(String basePackagePath, URI jarUri) {
        try {
            return FileSystems.newFileSystem(jarUri, Map.of()).getPath(basePackagePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String uriToString(URI uri) {
        return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }

    private String removeLeadingSlash(String s) {
        if (s.startsWith("/") || s.startsWith("\\")) {
            s = s.substring(1);
        }
        return s;
    }

    private String removeTrailingSlash(String string) {
        if (string.endsWith("/") || string.endsWith("\\")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    private ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return cl;
    }
}
