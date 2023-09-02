package com.chua.common.support.objects.source;

import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.spi.ServiceProvider;

import java.util.List;

/**
 * 来源工厂
 *
 * @author CH
 * @since 2023/09/02
 */
public class TypeDefinitionSourceFactory implements ObjectContext {

    private final ConfigureContextConfiguration configuration;
    final List<TypeDefinitionSource> definitionSources;

    public TypeDefinitionSourceFactory(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
        this.definitionSources = ServiceProvider.of(TypeDefinitionSource.class).collect(configuration);
    }

}
