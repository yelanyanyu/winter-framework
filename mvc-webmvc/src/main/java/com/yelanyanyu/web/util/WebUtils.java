package com.yelanyanyu.web.util;

import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.util.ClassPathUtils;
import com.yelanyanyu.util.YamlUtils;
import com.yelanyanyu.web.DispatcherServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Map;
import java.util.Properties;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class WebUtils {
    static final Logger logger = LoggerFactory.getLogger(WebUtils.class);
    static final String CONFIG_APP_YAML = "application.yml";
    static final String CONFIG_APP_PROP = "application.properties";

    /**
     * Get the PropertyResolver through .yaml(default) or .properties
     *
     * @return rr
     */
    public static PropertyResolver createPropertyResolver() {
        final Properties prop = new Properties();
        try {
            Map<String, Object> map = YamlUtils.loadYamlMapAsPlainMap(CONFIG_APP_YAML);
            logger.info("load config: {}", CONFIG_APP_YAML);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    prop.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            logger.info("load config: {}", CONFIG_APP_PROP);
            ClassPathUtils.readInputStream(CONFIG_APP_PROP, input -> {
                prop.load(input);
                return input;
            });
        }
        return new PropertyResolver(prop);
    }

    /**
     * Register dispatcherServlet into servlet context
     *
     * @param ioc              app context
     * @param propertyResolver res
     * @param servletContext   servlet context
     */
    public static void registerDispatcherServlet(ApplicationContext ioc,
                                                 PropertyResolver propertyResolver,
                                                 ServletContext servletContext) {
        DispatcherServlet dispatcherServlet = new DispatcherServlet(ioc, propertyResolver);
        logger.info("register servlet {} for URL '/'", dispatcherServlet.getClass().getName());
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        servlet.addMapping("/");
        servlet.setLoadOnStartup(0);
    }
}
