package com.yelanyanyu.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class PackageUtil {
    public static List<Class<?>> getClassesForPackage(final String pkgName) throws IOException, URISyntaxException {
        final String pkgPath = pkgName.replace('.', '/');
        final URI pkg = Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResource(pkgPath)).toURI();
        final ArrayList<Class<?>> allClasses = new ArrayList<Class<?>>();

        Path root;
        if (pkg.toString().startsWith("jar:")) {
            try {
                root = FileSystems.getFileSystem(pkg).getPath(pkgPath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(pkg, Collections.emptyMap()).getPath(pkgPath);
            }
        } else {
            root = Paths.get(pkg);
        }

        final String extension = ".class";
        try (final Stream<Path> allPaths = Files.walk(root)) {
            allPaths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    final String path = file.toString().replace('\\', '.');
                    final String name = path.substring(path.indexOf(pkgName), path.length() - extension.length());
                    allClasses.add(Class.forName(name));
                } catch (final ClassNotFoundException | StringIndexOutOfBoundsException ignored) {
                }
            });
        }
        return allClasses;
    }

    public static List<Class<?>> getClassesForPackage(final Package pkg) throws IOException, URISyntaxException {
        return getClassesForPackage(pkg.getName());
    }

    public static void main(final String[] argv) throws IOException, URISyntaxException {
        for (final Class<?> cls : getClassesForPackage("org/yaml/snakeyaml")) {
            System.out.println(cls);
        }
        System.out.println("=================================");
        for (final Class<?> cls : getClassesForPackage(PackageUtil.class.getPackage())) {
            System.out.println(cls);
        }
//        ClassLoader cl = Thread.currentThread().getContextClassLoader();
//        Enumeration<URL> resources = cl.getResources("org/yaml");
//        while (resources.hasMoreElements()) {
//            URL url = resources.nextElement();
//            System.out.println(url);
//        }
    }
}
