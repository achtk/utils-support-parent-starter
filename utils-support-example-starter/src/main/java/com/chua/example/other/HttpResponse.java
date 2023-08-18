package com.chua.example.other;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * 请求
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
@Builder
public class HttpResponse {
    /**
     * 错误码
     */
    private int code;
    /**
     * 数据
     */
    private Object content;
    /**
     * 错误消息
     */
    private String message;

    public <T>T to(Class<T> targetType) {
        if(String.class.isAssignableFrom(targetType) && content instanceof byte[]) {
            return (T) new String((byte[])content, StandardCharsets.UTF_8);
        }
        //TODO: spring ConversionService转化
        return null;
    }
}
