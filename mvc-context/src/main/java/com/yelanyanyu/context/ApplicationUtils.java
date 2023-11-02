package com.yelanyanyu.context;

import com.yelanyanyu.context.ApplicationContext;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Objects;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
public class ApplicationUtils {
    private static ApplicationContext applicationContext = null;
    @Nonnull
    public static ApplicationContext getRequiredApplicationContext() {
        return Objects.requireNonNull(getApplicationContext(), "Application is not set.");
    }

    @Nullable
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }
}
