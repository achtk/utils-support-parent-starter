package com.chua.common.support.file.export.resolver;

/**
 * 对象转化器
 *
 * @author CH
 */
public interface ValueResolver {
    /**
     * 转换
     *
     * @param o 数据
     * @return 类型
     */
    Object resolve(Object o);
}
