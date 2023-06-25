package com.chua.common.support.mapping;

import com.chua.common.support.annotations.Extension;
import com.chua.common.support.database.metadata.DelegateMetadata;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.mapping.annotation.MappingRequest;
import com.chua.common.support.mapping.database.Resolver;
import com.chua.common.support.mapping.database.SqlResolver;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * 数据库实体查询
 * @author CH
 */
@Extension("database")
public class DatabaseMappingResolver implements MappingResolver {

    private final Profile profile;
    private final Metadata<?> metadata;
    private final DataSource dataSource;

    public DatabaseMappingResolver(Profile profile) {
        this.profile = profile;
        this.dataSource = profile.getType("dataSource", null, DataSource.class);
        this.metadata = new DelegateMetadata<>(profile.getForType("type"), profile.getString("prefix"), profile.getString("suffix"));
    }
    @Override
    public <T> T create(Class<T> target) {
        return ProxyUtils.proxy(target, target.getClassLoader(), new DelegateMethodIntercept<T>(target, proxyMethod -> {
            if(proxyMethod.isDefault()) {
                return proxyMethod.doDefault();
            }
            Method method = proxyMethod.getMethod();
            if(method.isAnnotationPresent(MappingRequest.class) ) {
                MappingRequest mappingRequest = method.getDeclaredAnnotation(MappingRequest.class);
                return new SqlResolver(proxyMethod.getMethod(),  mappingRequest.value())
                        .resolve(dataSource, proxyMethod.getArgs(), metadata);
            }

            Resolver resolver = ServiceProvider.of(Resolver.class).getNewExtension(method.getName());
            return resolver.resolve(dataSource, proxyMethod.getArgs(), metadata);
        }));
    }
}
