package com.yelanyanyu.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@FunctionalInterface
public interface InputStreamCallback<T> {
    T apply(InputStream inputStream) throws IOException;
}
