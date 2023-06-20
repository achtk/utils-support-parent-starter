package com.chua.common.support.database.resolver;

import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.sqldialect.Dialect;

/**
 * 元数据解析器
 *
 * @author CH
 */
public interface MetadataResolver {
    /**
     * 解析元数据
     *
     * @param metadata 元数据
     * @param dialect  数据方言
     * @return 结果
     */
    MetadataExecutor resolve(Metadata<?> metadata, Dialect dialect);
}
