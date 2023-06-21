package com.chua.common.support.modularity;

import com.chua.common.support.converter.Converter;
import lombok.Builder;
import lombok.Data;

/**
 * 模块调用结果
 * @author CH
 */
@Data
@Builder
public class ModularityResult {
    public static final ModularityResult INSTANCE = ModularityResult.builder().build();
    /**
     * 响应码
     */
    private String code;
    /**
     * 响应结果
     */
    private Object data;
    /**
     * 响应消息
     */
    private String msg;

    public <T>T getData(Class<T> target) {
        return Converter.convertIfNecessary(data, target);
    }
}
