package com.chua.common.support.bean;

/**
 * 特性处理器
 *
 * @author CH
 */
public interface ReadFeatureHandler {
    /**
     * 解析结果
     *
     * @param value 值
     * @return 结果
     */
    Object handle(Object value);
}
