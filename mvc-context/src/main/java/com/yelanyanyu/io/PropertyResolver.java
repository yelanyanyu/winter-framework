package com.yelanyanyu.io;

import cn.hutool.core.lang.Assert;
import com.yelanyanyu.exception.UnmatchableException;
import com.yelanyanyu.util.YamlUtils;
import jakarta.annotation.Nullable;

import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class PropertyResolver {
    Map<Class<?>, Function<String, Object>> converters = new HashMap<>();
    Map<String, String> properties = new HashMap<>();

    public PropertyResolver(Properties props) {
        this.properties.putAll(System.getenv());
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            this.properties.put(key, props.getProperty(key));
        }

        converters.put(String.class, s -> s);
        converters.put(int.class, Integer::parseInt);
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(double.class, Double::parseDouble);
        converters.put(long.class, Long::parseLong);
        converters.put(Integer.class, Integer::parseInt);
        converters.put(Boolean.class, Boolean::parseBoolean);
        converters.put(Double.class, Double::parseDouble);
        converters.put(Long.class, Long::parseLong);

        converters.put(LocalDate.class, LocalDate::parse);
        converters.put(LocalTime.class, LocalTime::parse);
        converters.put(LocalDateTime.class, LocalDateTime::parse);
        converters.put(ZonedDateTime.class, ZonedDateTime::parse);
        converters.put(Duration.class, Duration::parse);
        converters.put(ZoneId.class, ZoneId::of);
        converters.put(Date.class, Date::parse);
    }

    <T> T convert(Class<?> clazz, String value) {
        Function<String, Object> func = this.converters.get(clazz);
        if (func == null) {
            throw new IllegalArgumentException("Unsupported value type: " + clazz.getName());
        }
        return (T) func.apply(value);
    }

    @Nullable
    public <T> T getProperty(String key, Class<T> classType) {
        String property = getProperty(key);
        if (property == null) {
            return null;
        }
        return convert(classType, property);
    }

    @Nullable
    public String getProperty(String key) {
        String property0 = getProperty0(key);
        if (property0 != null && (property0.startsWith("${") || property0.endsWith("}"))) {
            throw new UnmatchableException("wrong property format for " + key);
        }
        return property0;
    }

    /**
     * 根据key得到属性，支持嵌套查询
     *
     * @param key 键
     * @return 值
     */
    @Nullable
    String getProperty0(String key) {
        PropertyExpr propertyExpr = parsePropertyExpr(key);
        if (propertyExpr != null) {
            if (propertyExpr.defaultValue() == null) {
                return getPropertyWithNoneDefaultV(propertyExpr.key());
            } else {
                return getPropertyWithDefaultV(propertyExpr.defaultValue());
            }
        }
        return null;
    }

    @Nullable
    private String getPropertyWithNoneDefaultV(String key) {
        return this.properties.get(key);
    }

    /**
     * 根据默认值得到value, 如果有嵌套，就用嵌套的值来代替默认值，如果没有就用默认值
     *
     * @param defaultValue 默认值
     * @return 值
     */
    @Nullable
    private String getPropertyWithDefaultV(String defaultValue) {
        PropertyExpr propertyExpr = parsePropertyExpr(defaultValue);
        if (propertyExpr == null) {
            return defaultValue;
        }
        return getPropertyWithDefaultV(propertyExpr.defaultValue());
    }

    /**
     * @param key 解析形如 ${a:default} 的key
     * @return
     */
    private PropertyExpr parsePropertyExpr(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            int idxN = key.indexOf(":");
            String defaultValue, k;
            if (idxN == -1) {
                defaultValue = null;
                k = key.substring(2, key.length() - 1);
            } else {
                defaultValue = key.substring(idxN + 1, key.length() - 1);
                k = key.substring(2, idxN);
            }
            return new PropertyExpr(k, defaultValue);
        }
        return null;
    }

}
