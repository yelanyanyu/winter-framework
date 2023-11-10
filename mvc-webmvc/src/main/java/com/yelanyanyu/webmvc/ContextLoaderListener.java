package com.yelanyanyu.webmvc;

import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.webmvc.exception.NestedRuntimeException;
import com.yelanyanyu.webmvc.util.WebUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ContextLoaderListener implements ServletContextListener {
    final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_APPLICATION_CONTEXT_NAME = "applicationContext";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("init: {}", getClass().getName());
        ServletContext servletContext = servletContextEvent.getServletContext();
        WebMvcConfiguration.setServletContext(servletContext);
        String configClassName = servletContext.getInitParameter("configuration");
        // get application.properties
        PropertyResolver propertyResolver = WebUtils.createPropertyResolver();
        ApplicationContext applicationContext = createApplicationContext(configClassName, propertyResolver);

        // register dispatcherServlet
        WebUtils.registerDispatcherServlet(applicationContext, propertyResolver, servletContext);


        servletContext.setAttribute("applicationContext", applicationContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (servletContextEvent.getServletContext().getAttribute(DEFAULT_APPLICATION_CONTEXT_NAME) instanceof ApplicationContext ioc) {
            ioc.close();
        }
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
