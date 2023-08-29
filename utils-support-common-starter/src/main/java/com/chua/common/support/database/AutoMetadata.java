package com.chua.common.support.database;

import com.chua.common.support.database.annotation.Table;
import com.chua.common.support.database.executor.MetadataExecutor;
import com.chua.common.support.database.metadata.DelegateMetadata;
import com.chua.common.support.database.repository.Repository;
import com.chua.common.support.database.resolver.DelegateMetadataResolver;
import com.chua.common.support.database.resolver.MetadataResolver;
import com.chua.common.support.database.sqldialect.Dialect;
import com.chua.common.support.lang.pipeline.PipelineBuilder;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.mapping.MappingProxy;
import com.chua.common.support.utils.ClassUtils;
import lombok.Builder;

import javax.sql.DataSource;
import java.util.Map;

import static com.chua.common.support.constant.NameConstant.MASTER;

/**
 * 自动建表
 *
 * @author CH
 */
@Builder
public class AutoMetadata {

    private static final String HIBERNATE = "com.chua.hibernate.support.database.resolver.HibernateMetadataResolver";
    @Builder.Default
    private MetadataResolver metadataResolver = PipelineBuilder.<MetadataResolver>newBuilder()
            .next(it -> {
                if (null != it) {
                    return it;
                }

                if (ClassUtils.isPresent(HIBERNATE)) {
                    return (MetadataResolver) ClassUtils.forObject(HIBERNATE);
                }
                return null;
            }).next(it -> new DelegateMetadataResolver()).execute();

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

    @SuppressWarnings("ALL")
    public <T> Repository<T> createRepository(Map<String, DataSource> dataSources,
                                              Class<T> type) {

        return MappingProxy.create("database", Repository.class,
                Profile.newDefault()
                        .addProfile("dataSource", getDataSource(dataSources, type))
                        .addProfile("type", type)
                        .addProfile("suffix", suffix)
                        .addProfile("prefix", prefix)
        );
    }

    private DataSource getDataSource(Map<String, DataSource> dataSources, Class<?> type) {
        if(dataSources.isEmpty()) {
            throw new RuntimeException("数据源不存在");
        }

        if (dataSources.size() == 1) {
            return dataSources.values().stream().findFirst().get();
        }
        Table table = type.getDeclaredAnnotation(Table.class);
        String schema = table.schema();
        if (dataSources.containsKey(schema)) {
            return dataSources.get(schema);
        }

        if (dataSources.containsKey(MASTER)) {
            return dataSources.get(MASTER);
        }

        return dataSources.values().stream().findFirst().get();

    }

    @SuppressWarnings("ALL")
    public <T> Repository<T> createRepository(DataSource dataSource,
                                                      Class<T> type) {
        return MappingProxy.create("database", Repository.class,
                Profile.newDefault()
                        .addProfile("dataSource", dataSource)
                        .addProfile("type", type)
                        .addProfile("suffix", suffix)
                        .addProfile("prefix", prefix)
        );
    }
    @SuppressWarnings("ALL")
    public <T, R extends Repository<T>> R createRepository(DataSource dataSource,
                                                      Class<T> type, Class<R> r) {
        return MappingProxy.create("database", r,
                Profile.newDefault()
                        .addProfile("dataSource", dataSource)
                        .addProfile("type", type)
                        .addProfile("suffix", suffix)
                        .addProfile("prefix", prefix)
        );
    }
}
