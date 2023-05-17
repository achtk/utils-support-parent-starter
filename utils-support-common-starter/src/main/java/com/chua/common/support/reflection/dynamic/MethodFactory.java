package com.chua.common.support.reflection.dynamic;

import java.util.function.Function;

/**
 * 注解工厂
 *
 * @author CH
 */
public interface MethodFactory extends Function<String, String> {
    /**
     * 获取方法体
     *
     * @param methodName 方法名
     * @return 方法体
     */
    String body(String methodName);

    /**
     * 获取方法体
     *
     * @param s 方法名
     * @return 方法体
     */
    @Override
    default String apply(String s) {
        return body(s);
    }
}
