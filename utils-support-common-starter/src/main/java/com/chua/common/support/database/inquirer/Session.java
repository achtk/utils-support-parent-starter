package com.chua.common.support.database.inquirer;

import java.io.Serializable;

/**
 * 会话
 * @author CH
 */
public interface Session extends AutoCloseable{
    /**
     * 根据主键获取结果
     * @param javaType 类型
     * @param id id
     * @return 结果
     * @param <T> 类型
     */
    <T>T get(Class<T> javaType, Serializable id);
}
