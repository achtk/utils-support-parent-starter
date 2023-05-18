package com.chua.common.support.database.actuator;

import com.chua.common.support.database.metadata.Metadata;

/**
 * 执行器
 *
 * @author CH
 */
public interface Actuator {

    /**
     * 執行
     *
     * @param name 名称
     * @param args 参数
     * @return 结果
     */
    Object doExecute(String name, Object... args);

    /**
     * 获取元数据
     *
     * @param metadata 比较数据
     * @param key      key
     * @return 元数据
     */
    Metadata<?> getTable(Metadata<?> metadata, Object key);
}
