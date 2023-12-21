package com.yelanyanyu.boot;

import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.web.ContextLoaderInitializer;
import com.yelanyanyu.web.util.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.Server;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.Set;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class WinterApplication {
    static final String CONFIG_APP_YAML = "/application.yml";
    static final String CONFIG_APP_PROP = "/application.properties";

    public static void run(String webDir, String baseDir, Class<?> configClass, String... args) throws Exception {
        new WinterApplication().start(webDir, baseDir, configClass, args);
    }

    public static void run(Class<?> configClass, String... args) {

    }

    public void start(String webDir, String baseDir, Class<?> configClass, String... args) throws Exception {
        // start info:
        final long startTime = System.currentTimeMillis();
        final int javaVersion = Runtime.version().feature();
        final long pid = ManagementFactory.getRuntimeMXBean().getPid();
        final String user = System.getProperty("user.name");
        final String pwd = Paths.get("").toAbsolutePath().toString();
        log.info("Starting {} using Java {} with PID {} (started by {} in {})", configClass.getSimpleName(), javaVersion, pid, user, pwd);

        var propertyResolver = WebUtils.createPropertyResolver();
        Server server = startTomcat(webDir, baseDir, configClass, propertyResolver);

        // started info:
        final long endTime = System.currentTimeMillis();
        final String appTime = String.format("%.3f", (endTime - startTime) / 1000.0);
        final String jvmTime = String.format("%.3f", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
        log.info("Started {} in {} seconds (process running for {})", configClass.getSimpleName(), appTime, jvmTime);

        server.await();
    }

    protected Server startTomcat(String webDir, String baseDir, Class<?> configClass, PropertyResolver propertyResolver) throws Exception {
        int port = propertyResolver.getProperty("${server.port:8080}", int.class);
        String contextPath = propertyResolver.getProperty("${server.context-path:}");
        log.info("starting Tomcat at port {}...", port);
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector().setThrowOnFailure(true);
        Context ctx = tomcat.addWebapp(contextPath, new File(webDir).getAbsolutePath());

        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", new File(baseDir).getAbsolutePath(), "/"));
        ctx.setResources(resources);
        ctx.addServletContainerInitializer(new ContextLoaderInitializer(configClass, propertyResolver), Set.of());

        tomcat.start();
        log.info("Tomcat started at port {}...", port);

        return tomcat.getServer();
    }
}
