package com.yelanyanyu.webmvc.util;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public class JsonUtils {
    public static Object readerToBean(BufferedReader reader, Class<?> type) {
        StringBuilder builder = new StringBuilder();
        String line;
        try (reader) {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return JSONUtil.toBean(builder.toString(), type);
    }

    /**
     * write object to 'writer' stream, and do not close it.
     *
     * @param writer w
     * @param object o
     */
    public static void writeJson(Writer writer, Object object) {
        JSONUtil.toJsonStr(object, writer);
    }

    public static String writeToString(Object object) {
        return JSONUtil.toJsonStr(object);
    }
}
