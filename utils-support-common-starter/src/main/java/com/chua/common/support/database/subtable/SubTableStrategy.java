package com.chua.common.support.database.subtable;

import com.chua.common.support.range.Range;

import java.util.Collection;

/**
 * 分表测率
 *
 * @author CH
 */
public interface SubTableStrategy {
    /**
     * 获取策略
     *
     * @return 获取策略
     */
    Strategy getStrategy();

    /**
     * 获取字段
     *
     * @return 获取字段
     */
    String getColumn();

    /**
     * 分析名称
     *
     * @param collection     表集合
     * @param logicTableName 逻辑表名
     * @param value          数据
     * @return 实际表名
     */
    String doSharding(Collection<String> collection, String logicTableName, String value);

    /**
     * 分析名称
     *
     * @param collection     表集合
     * @param logicTableName 逻辑表名
     * @param value          数据
     * @return 实际表名
     */
    Collection<String> doSharding(Collection<String> collection, String logicTableName, Range<String> value);
}
