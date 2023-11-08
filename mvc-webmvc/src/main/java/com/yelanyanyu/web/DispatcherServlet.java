package com.yelanyanyu.web;

import com.yelanyanyu.annotation.Controller;
import com.yelanyanyu.annotation.GetMapping;
import com.yelanyanyu.annotation.PostMapping;
import com.yelanyanyu.annotation.RestController;
import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.context.BeanDefinition;
import com.yelanyanyu.context.ConfigurableApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class DispatcherServlet extends HttpServlet {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    ApplicationContext ioc;
    String resourcePath;
    String faviconPath;
    List<Dispatcher> getDispatchers = new ArrayList<>();
    List<Dispatcher> postDispatchers = new ArrayList<>();
    PropertyResolver propertyResolver;

    public DispatcherServlet(ApplicationContext ioc, PropertyResolver propertyResolver) {
        this.ioc = ioc;
        this.propertyResolver = propertyResolver;
        this.resourcePath = propertyResolver.getProperty("${webmvc.resource-path:static/}");
        this.faviconPath = propertyResolver.getProperty("${webmvc.favicon-path:favicon/}");
    }

    /**
     * Obtain all the classes annotated with @Controller and @RestController
     *
     * @param config config
     * @throws ServletException exc
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.info("init: {}", getClass().getName());
        for (BeanDefinition def : ((ConfigurableApplicationContext) ioc).findBeanDefinitions(Object.class)) {
            Class<?> beanClass = def.getBeanClass();
            Controller controller = beanClass.getAnnotation(Controller.class);
            RestController restController = beanClass.getAnnotation(RestController.class);
            if (controller != null && restController != null) {
                throw new ServletException("both @Controller and @RestController found in bean " + def.getName());
            }
            if (controller != null) {
                addController(false, def.getName(), def.getInstance());
            }
            if (restController != null) {
                addController(true, def.getName(), def.getInstance());
            }
        }
    }

    /**
     * add controllers into the get or post dispatcher's list, init the dispatcher
     *
     * @param isRest   judge whether it is annotated with @RestController
     * @param beanName beanName to log
     * @param bean     bean instance
     */
    void addController(boolean isRest, String beanName, Object bean) throws ServletException {
        logger.info("add {} controller: {} ", isRest ? "REST" : "MVC", beanName);
        // Bear in mind that a bean instance could either be a proxy
        // instance or the original instance. So, def.getBeanClass()
        // is not a good idea and bean.getClass() is great.
        addMethods(isRest, bean, bean.getClass());
    }


    /**
     * Truly add rest or mvc controller into getDispatcher or postDispatcher.
     * Scanning all valid methods in 'bean' that annotated with @GetMapping or @PostMapping.
     *
     * @param isRest    param
     * @param bean      param
     * @param beanClass param
     */
    void addMethods(boolean isRest, Object bean, Class<?> beanClass) throws ServletException {
        for (Method m : beanClass.getDeclaredMethods()) {
            checkMappingMethod(m);
            GetMapping getMapping = m.getAnnotation(GetMapping.class);
            PostMapping postMapping = m.getAnnotation(PostMapping.class);
            if (getMapping != null && postMapping != null) {
                throw new ServletException(String.format(
                        "found @GetMapping and @PostMapping in method %s: %s", beanClass.getName(), m.getName()));
            }
            if (getMapping != null) {
                getDispatchers.add(new Dispatcher("GET", isRest, bean, m, getMapping.value()));
            }
            if (postMapping != null) {
                postDispatchers.add(new Dispatcher("POST", isRest, bean, m, postMapping.value()));
            }
        }
    }

    void checkMappingMethod(Method method) throws ServletException {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            throw new ServletException(String.format(
               "Cannot do URL mapping to static method: %s",
                    method.getName()
            ));
        }
        method.setAccessible(true);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        if (url.startsWith(this.resourcePath) || url.equals(this.faviconPath)) {
            doResource(url, req, resp);
        } else {
            doService(url, req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doService(req.getRequestURI(), req, resp);
    }

    /**
     * Handling requests directed towards static resources
     *
     * @param url  url
     * @param req  req
     * @param resp resp
     */
    void doResource(String url, HttpServletRequest req, HttpServletResponse resp) {
        //todo
    }

    /**
     * Handling requests directed towards dynamic resources
     *
     * @param url  url
     * @param req  req
     * @param resp resp
     */
    void doService(String url, HttpServletRequest req, HttpServletResponse resp) {
        //todo
    }
}
