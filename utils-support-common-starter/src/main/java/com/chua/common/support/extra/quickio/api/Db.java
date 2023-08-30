package com.chua.common.support.extra.quickio.api;

import com.chua.common.support.extra.quickio.core.IoEntity;

/**
 * 数据库
 * @author Administrator
 */
public interface Db extends AutoCloseable {
    /**
     * 关闭
     */
    @Override
    void close();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 获取数据
     * @param clazz 类型
     * @return 数据
     * @param <T> 类型
     */
    <T extends IoEntity> Collection<T> collection(Class<T> clazz);
}
