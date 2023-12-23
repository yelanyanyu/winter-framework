package com.yelanyanyu.web;

import com.yelanyanyu.boot.web.servlet.FilterRegistrationBean;
import com.yelanyanyu.context.AnnotationConfigApplicationContext;
import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.context.ApplicationUtils;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.web.util.WebUtils;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class ContextLoaderInitializer implements ServletContainerInitializer {
    final Class<?> configClass;
    final PropertyResolver propertyResolver;

    public ContextLoaderInitializer(Class<?> configClass, PropertyResolver propertyResolver) {
        this.configClass = configClass;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        WebMvcConfiguration.setServletContext(servletContext);
        // Start the application context.
        ApplicationContext ioc = new AnnotationConfigApplicationContext(configClass, propertyResolver);
        log.info("ioc: {}", ioc);
        registerFilters(servletContext);
        WebUtils.registerDispatcherServlet(servletContext, propertyResolver);
    }

    void registerFilters(final ServletContext ctx) {
        // add dynamic filter and make sure the order of filters.
        final ApplicationContext ioc = ApplicationUtils.getRequiredApplicationContext();
        final List<FilterRegistrationBean> regBeans = ioc.getBeans(FilterRegistrationBean.class);
        for (FilterRegistrationBean filterConfig : regBeans) {
            log.debug("filter: {}", filterConfig.getFilter());
            FilterRegistration.Dynamic dynamic = ctx.addFilter(filterConfig.getName(), filterConfig.getFilter());
            Map initParams = filterConfig.getInitParams();
            if (initParams != null && !initParams.isEmpty()) {
                dynamic.setInitParameters(filterConfig.getInitParams());
            }

            Set<String> set = filterConfig.getUrlPatterns();
            String[] urlPatterns = new String[set.size()];
            int index = 0;
            for (String o : set) {
                urlPatterns[index++] = o;
            }

            dynamic.addMappingForUrlPatterns(filterConfig.getDispatcherTypes(),
                    filterConfig.isMatchAfter(),
                    urlPatterns);
            log.debug("filter: {} registered.", filterConfig.getFilter().getClass().getSimpleName());
        }
    }
}
