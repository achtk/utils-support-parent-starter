package com.chua.common.support.mapping;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonPath;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.log.Log;
import com.chua.common.support.utils.StringUtils;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 请求
 *
 * @author CH
 */
@Data
@Builder
public class Response {

    private Log log;

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
        this.log = Log.getLogger(proxyMethod.getMethod().getDeclaringClass());
        return convertResponse(execute, proxyMethod);
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
        if (Collection.class.isAssignableFrom(returnType)) {
            String format = ((String) format(execute)).trim();
            if (format.startsWith(SYMBOL_LEFT_BIG_PARENTHESES)) {
                format = SYMBOL_LEFT_SQUARE_BRACKET + format + SYMBOL_RIGHT_SQUARE_BRACKET;
            }

            return formatResult(format, Collection.class);
        }

        if (Map.class.isAssignableFrom(returnType)) {
            String format = ((String) format(execute)).trim();
            if (format.startsWith(SYMBOL_LEFT_SQUARE_BRACKET)) {
                format = format.substring(1, format.length() - 1);
            }

            return formatResult(format, Map.class);
        }
        if(Converter.hasConverter(returnType)) {
            return Converter.convertIfNecessary(format(execute), returnType);
        }
        return Json.fromJson((String) format(execute), returnType);
    }

    /**
     * 格式化结果
     *
     * @param format       总体安排
     * @param targetTarget 集合类
     * @return {@link Object}
     */
    private Object formatResult(String format, Class<?> targetTarget) {
        if (Collection.class.isAssignableFrom(targetTarget)) {
            if(Collection.class.isAssignableFrom(returnType)) {
                log.info("请指定List的类型, @com.chua.common.support.mapping.annotations.MappingRequest.returnType");
                return Collections.unmodifiableList(Json.fromJsonToList(format, JSONObject.class));
            }
            return Collections.unmodifiableList(Json.fromJsonToList(format, returnType));
        }

        return Json.fromJson(format, returnType);
    }

    /**
     * 总体安排
     *
     * @param execute 处决
     * @return {@link Object}
     */
    private Object format(Object execute) {
        String value = Converter.convertIfNecessary(execute, String.class);
        if (StringUtils.isNotBlank(jsonPath)) {
            value = Converter.convertIfNecessary(JsonPath.read(value, jsonPath), String.class);
        }

        return value;
    }
}
