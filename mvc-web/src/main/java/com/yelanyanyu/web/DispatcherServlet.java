package com.yelanyanyu.web;

import com.yelanyanyu.context.ApplicationContext;
import com.yelanyanyu.context.BeanDefinition;
import com.yelanyanyu.context.ConfigurableApplicationContext;
import com.yelanyanyu.io.PropertyResolver;
import com.yelanyanyu.web.annotation.Controller;
import com.yelanyanyu.web.annotation.GetMapping;
import com.yelanyanyu.web.annotation.PostMapping;
import com.yelanyanyu.web.annotation.RestController;
import com.yelanyanyu.web.bean.Result;
import com.yelanyanyu.web.util.JsonUtils;
import com.yelanyanyu.web.view.View;
import com.yelanyanyu.web.view.ViewResolver;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class DispatcherServlet extends HttpServlet {
    private static final String FORWARD_URL_PREFIX = "forward:";
    private static final String REDIRECT_URL_PREFIX = "redirect:";
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    List<ViewResolver> viewResolvers;
    ApplicationContext ioc;
    String resourcePath;
    String faviconPath;
    List<Dispatcher> getDispatchers = new ArrayList<>();
    List<Dispatcher> postDispatchers = new ArrayList<>();
    PropertyResolver propertyResolver;
    ServletContext servletContext;
    String contextPath;

    public DispatcherServlet(ApplicationContext ioc, PropertyResolver propertyResolver) {
        this.ioc = ioc;
        this.propertyResolver = propertyResolver;
        this.viewResolvers = ioc.getBeans(ViewResolver.class);
        this.servletContext = ioc.getBean(ServletContext.class);
        this.contextPath = servletContext.getContextPath();
        logger.debug("contextPath: {}", this.contextPath);
        this.resourcePath = this.contextPath + propertyResolver.getProperty("${winter.web.resource-path:/static/}");
        this.faviconPath = this.contextPath + propertyResolver.getProperty("${winter.web.favicon-path:/favicon/}");
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
                getDispatchers.add(new Dispatcher("GET", isRest, bean, m, addContextPathToMapping(getMapping.value())));
            }
            if (postMapping != null) {
                postDispatchers.add(new Dispatcher("POST", isRest, bean, m, addContextPathToMapping(postMapping.value())));
            }
        }
    }

    String addContextPathToMapping(String mappingValue) {
        String contextPath = this.servletContext.getContextPath();
        if (contextPath == null) {
            return mappingValue;
        }
        return contextPath + mappingValue;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String url = req.getRequestURI();
        if (url.startsWith(this.resourcePath) || url.equals(this.faviconPath)) {
            doResource(url, req, resp);
        } else {
            doService(req, resp, getDispatchers);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doService(req, resp, postDispatchers);
    }

    /**
     * Handling requests directed towards static resources
     *
     * @param url  url
     * @param req  req
     * @param resp resp
     */
    void doResource(String url, HttpServletRequest req, HttpServletResponse resp) {
        ServletContext servletContext = req.getServletContext();
        try (InputStream input = servletContext.getResourceAsStream(url)) {
            if (input == null) {
                resp.sendError(404, "Not Found");
                return;
            }
            // return the resource
            String file = url;
            int n = file.indexOf("/");
            if (n >= 0) {
                file = file.substring(n + 1);
            }
            String mimeType = servletContext.getMimeType(file);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            resp.setContentType(mimeType);
            ServletOutputStream os = resp.getOutputStream();
            input.transferTo(os);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handling requests directed towards dynamic resources.
     * Several things are completed:
     * 1. invoke the handler method;
     * 2.
     *
     * @param url  url
     * @param req  req
     * @param resp resp
     */
    void doService(String url, HttpServletRequest req, HttpServletResponse resp, List<Dispatcher> dispatcherList) throws ServletException, IOException {
        for (Dispatcher dispatcher : dispatcherList) {
            Result result = dispatcher.process(url, req, resp);
            // if the handler method is executed successfully
            if (result.processed()) {
                Object returnObj = result.returnObject();
                if (dispatcher.isRest) {
                    if (!resp.isCommitted()) {
                        resp.setContentType("application/json");
                    }
                    // send response body directly
                    if (dispatcher.isResponseBody) {
                        // send response body
                        if (returnObj instanceof String s) {
                            PrintWriter writer = resp.getWriter();
                            writer.write(s);
                            writer.flush();
                        } else if (returnObj instanceof byte[] bytes) {
                            resp.setContentType("application/octet-stream");
                            ServletOutputStream out = resp.getOutputStream();
                            out.write(bytes);
                            out.flush();
                        } else {
                            throw new ServletException(String.format(
                                    "Cannot process REST result when handle url: %s",
                                    url
                            ));
                        }
                    } else if (!dispatcher.isVoid) {
                        // other type that should be written to json str
                        PrintWriter writer = resp.getWriter();
                        JsonUtils.writeJson(writer, returnObj);
                        writer.flush();
                    }
                } else {
                    // process MVC, render view and send html back.
                    if (!resp.isCommitted()) {
                        resp.setContentType("text/html");
                    }
                    if (returnObj instanceof String s) {
                        if (dispatcher.isResponseBody) {
                            PrintWriter w = resp.getWriter();
                            w.write(s);
                            w.flush();
                        } else if (s.startsWith(REDIRECT_URL_PREFIX)) {
                            resp.sendRedirect(s.substring(REDIRECT_URL_PREFIX.length()));
                        } else {
                            throw new ServletException("Cannot process String result when handle url: " + url);
                        }
                    } else if (returnObj instanceof byte[] bytes) {
                        if (dispatcher.isResponseBody) {
                            resp.setContentType("application/octet-stream");
                            ServletOutputStream o = resp.getOutputStream();
                            o.write(bytes);
                            o.flush();
                        } else {
                            throw new ServletException("Cannot process byte array result when handle url: " + url);
                        }
                    } else if (returnObj instanceof ModelAndView mv) {
                        // invoke the ViewResolver. Send html back.
                        logger.debug("begin render model and view: {}", mv);
                        render(mv, req, resp);
                    } else if (!dispatcher.isVoid && returnObj != null) {
                        if (!dispatcher.isResponseBody) {
                            throw new ServletException(String.format(
                                    "The handler '%s' is incapable of processing the returned object",
                                    url
                            ));
                        }
                        resp.setContentType("application/json");
                        PrintWriter w = resp.getWriter();
                        w.write(JsonUtils.writeToString(returnObj));
                        w.flush();
                    }
                }
            }
        }
        if (!resp.isCommitted()) {
            resp.sendError(404, "Not found");
        }
    }

    /**
     * go to the view, and set model to request attribute
     *
     * @param mv .
     * @param req .
     * @param resp .
     */
    protected void render(ModelAndView mv, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String viewName = mv.getViewName();
        View view;
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            try {
                resp.sendRedirect(viewName.substring(REDIRECT_URL_PREFIX.length()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            view = resolveViewName(viewName, mv.getModel(), req, resp);
            if (view == null) {
                logger.error("viewName: {}", viewName);
                logger.error("viewResolvers: {}", this.viewResolvers);
                throw new ServletException("Unable to resolve viewName " + viewName);
            }
            // do forward
            try {
                view.render(mv.getModel(), req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * invoke all the viewResolver injected into the app context to apply correctly view.
     * In other words, it involves specifying the 'url' attribute within class 'AbstractView' in order to exec req.getRequestDispatcher(url).forward(req, resp).
     *
     * @param viewName .
     * @param model    .
     * @param req      .
     * @param resp     .
     * @return view
     */
    protected View resolveViewName(String viewName, Map<String, Object> model, HttpServletRequest req, HttpServletResponse resp) {
        if (this.viewResolvers == null) {
            return null;
        }
        for (ViewResolver viewResolver : this.viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, model, req, resp);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    void doService(HttpServletRequest req, HttpServletResponse resp, List<Dispatcher> dispatcherList) {
        String url = req.getRequestURI();
        try {
            doService(url, req, resp, dispatcherList);
        } catch (ServletException | IOException e) {
            logger.error("url: {}", url);
            throw new RuntimeException(e);
        }
    }
}
