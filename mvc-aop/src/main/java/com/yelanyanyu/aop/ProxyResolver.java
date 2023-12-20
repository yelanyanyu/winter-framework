package com.yelanyanyu.aop;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;

/**
 * @author yelanyanyu@zjxu.edu.cn
 * @version 1.0
 */
@Slf4j
public enum ProxyResolver {
    /**
     * Singleton
     */
    INSTANCE;
    private final ByteBuddy byteBuddy = new ByteBuddy();

    /**
     * Create a proxy instance for the original bean with the interceptor. The interceptor is used to modify the method behavior of the original bean, such as modifying the return value of the method.
     *
     * @param originBean Origin Bean
     * @param handler    InterceptorHandler
     * @return Proxy instance
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T originBean, InvocationHandler handler) {
        // 目标Bean的Class类型:
        Class<?> targetClass = originBean.getClass();
        // 动态创建Proxy的Class:
        Class<?> proxyClass = this.byteBuddy
                // 子类用默认无参数构造方法:
                .subclass(targetClass, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                // 拦截所有public方法:
                .method(ElementMatchers.isPublic()).intercept(InvocationHandlerAdapter.of(
                        // 新的拦截器实例:
                        (proxy, method, args) -> {
                            // 将方法调用代理至原始Bean:
                            return handler.invoke(originBean, method, args);
                        }))
                // 生成字节码:
                .make()
                // 加载字节码:
                .load(targetClass.getClassLoader()).getLoaded();
        // 创建Proxy实例:
        Object proxy;
        try {
            proxy = proxyClass.getConstructor().newInstance();
//            System.out.println("======================proxy: " + proxy);
        } catch (RuntimeException e) {
            log.error("RuntimeException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return (T) proxy;
    }
}
