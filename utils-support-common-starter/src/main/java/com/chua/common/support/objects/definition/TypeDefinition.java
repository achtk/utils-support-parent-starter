package com.chua.common.support.objects.definition;

/**
 * 定义
 * @author CH
 */
public interface TypeDefinition {
    /**
     * 获取类型
     * @return {@link Class}<{@link ?}>
     */
    Class<?> getType();

    /**
     * 獲取對象
     *
     * @return 对象
     */
    Object getObject();



}
