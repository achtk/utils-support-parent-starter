package com.chua.common.support.database.executor;


import com.chua.common.support.constant.Action;

/**
 * 元数据执行器
 *
 * @author CH
 */
public interface MetadataExecutor {

    /**
     * 执行
     *
     * @param datasource 数据源
     * @param action     动作
     */
    void execute(Object datasource, Action action);
    /**
     * 执行
     *
     * @param datasource 数据源
     */
    default void execute(Object datasource) {
        execute(datasource, Action.UPDATE);
    }
}
