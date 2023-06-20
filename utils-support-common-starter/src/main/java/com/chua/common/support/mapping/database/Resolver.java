package com.chua.common.support.mapping.database;

import com.chua.common.support.database.metadata.Metadata;

import javax.sql.DataSource;

/**
 * 解释器
 *
 * @author CH
 */
public interface Resolver {
    /**
     * 处理
     *
     * @param dataSource
     * @param args       参数
     * @param metadata   类型
     * @return 结果
     */
    Object resolve(DataSource dataSource, Object[] args, Metadata<?> metadata);

}
