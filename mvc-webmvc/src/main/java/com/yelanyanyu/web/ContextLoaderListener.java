package com.yelanyanyu.web;

import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.context.ApplicationUtils;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.web.exception.NestedRuntimeException;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ContextLoaderListener implements ServletContextListener {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("init: {}", getClass().getName());
        ServletContext servletContext = servletContextEvent.getServletContext();

        String configClassName = servletContext.getInitParameter("configuration");
        // 获取启动配置文件 application.properties


        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        servlet.addMapping("/");
        servlet.setLoadOnStartup(0);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    ApplicationContext createApplicationContext(String configClassName, PropertyResolver propertyResolver) {
        logger.info("init Application by configuration: {}", configClassName);
        if (StringUtils.isBlank(configClassName)) {
            throw new NestedRuntimeException("Cannot init application context without configClass");
        }
        Class<?> configClass;
        try {
            configClass = Class.forName(configClassName);
        } catch (ClassNotFoundException e) {
            throw new NestedRuntimeException("Cannot load class from from init param 'configuration': " + configClassName);
        }
        return new AnnotationConfigApplicationContext(configClass, propertyResolver);
    }
}
