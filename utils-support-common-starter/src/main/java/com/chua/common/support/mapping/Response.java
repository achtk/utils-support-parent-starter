package com.chua.common.support.mapping;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.proxy.ProxyMethod;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * 请求
 *
 * @author CH
 */
@Data
@Builder
public class Response {

    /**
     * json-path
     */
    private String jsonPath;

    /**
     * 响应类型
     */
    private Class<?> returnType;

    /**
     * 获取值
     *
     * @param execute     处决
     * @param proxyMethod 代理方法
     * @return {@link Object}
     */
    public Object getValue(Object execute, ProxyMethod proxyMethod) {
        return convertResponse(format(execute), proxyMethod);
    }

    /**
     * 转换响应
     *
     * @param execute     处决
     * @param proxyMethod 代理方法
     * @return {@link Object}
     */
    private Object convertResponse(Object execute, ProxyMethod proxyMethod) {
        Method method = proxyMethod.getMethod();
        Class<?> returnType = method.getReturnType();
        if(Collection.class.isAssignableFrom(returnType)) {
            String value = Converter.convertIfNecessary(execute, String.class);
            return null;
        }

        if(Map.class.isAssignableFrom(returnType)) {
            return null;
        }
        return Converter.convertIfNecessary(execute, returnType);
    }
    /**
     * 总体安排
     *
     * @param execute     处决
     * @return {@link Object}
     */
    private Object format(Object execute) {
        return execute;
    }
}
