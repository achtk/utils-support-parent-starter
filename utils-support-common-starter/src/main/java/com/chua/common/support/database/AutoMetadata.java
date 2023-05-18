package com.chua.common.support.database;

import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.metadata.DelegateMetadata;
import com.chua.common.support.database.resolver.DelegateMetadataResolver;
import com.chua.common.support.database.resolver.MetadataResolver;
import lombok.Builder;

/**
 * 自动建表
 * @author CH
 */
@Builder
public class AutoMetadata {

    @Builder.Default
    private MetadataResolver metadataResolver = new DelegateMetadataResolver();

    /**
     * 表前缀
     */
    @Builder.Default
    private String prefix = "";
    /**
     * 表后缀
     */
    @Builder.Default
    private String suffix = "";


    public MetadataExecutor doExecute(Class<?> type) {
        return metadataResolver.resolve(new DelegateMetadata<>(type, prefix, suffix), null);
    }

    public MetadataExecutor doExecute(Class<?> type, Dialect dialect) {
        return metadataResolver.resolve(new DelegateMetadata<>(type, prefix, suffix), dialect);
    }
}
