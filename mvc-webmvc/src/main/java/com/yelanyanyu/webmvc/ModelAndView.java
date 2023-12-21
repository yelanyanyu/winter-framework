package com.yelanyanyu.webmvc;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
@Data
public class ModelAndView {
    int status;
    private String view;
    private Map<String, Object> model;

    public ModelAndView(String viewName) {
        this(viewName, HttpServletResponse.SC_OK, null);
    }

    public ModelAndView(String viewName, @Nullable Map<String, Object> model) {
        this(viewName);
        if (model != null) {
            addModel(model);
        }
    }

    public ModelAndView(String viewName, int status, @Nullable Map<String, Object> model) {
        this.view = viewName;
        this.status = status;
        if (model != null) {
            addModel(model);
        }
    }

    public ModelAndView(String viewName, String modelName, Object modelObject) {
        this(viewName);
        addModel(modelName, modelObject);
    }

    public void addModel(String modelName, Object modelObject) {
        if (this.model == null) {
            this.model = new HashMap<>(16);
        }
        this.model.put(modelName, modelObject);
    }

    public void addModel(Map<String, Object> model) {
        if (this.model == null) {
            this.model = new HashMap<>(16);
        }
        this.model.putAll(model);
    }

    public String getViewName() {
        return (this.view instanceof String) ? (String) this.view : null;
    }


}
