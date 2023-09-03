package com.chua.common.support.objects.source;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ObjectContext;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.spi.ServiceProvider;

import java.util.List;

import static com.chua.common.support.objects.source.AbstractTypeDefinitionSource.COMPARABLE;

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

    @Override
    public <T> T getBean(String name, Class<T> targetType) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARABLE);
        for (TypeDefinitionSource definitionSource : definitionSources) {
            sortedList.addAll(definitionSource.getBean(name, targetType));
        }

        return sortedList.first().newInstance(this);
    }

    @Override
    public Object getBean(String name) {
        return null;
    }

    @Override
    public SortedList<TypeDefinition> getBeanDefinition(String name) {
        return null;
    }

    @Override
    public <T> ObjectProvider<T> getBean(Class<T> targetType) {
        return null;
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {

    }

    @Override
    public void unregister(String name, Class<? extends TypeDefinition> type) {

    }

    @Override
    public void register(TypeDefinition definition) {
        for (TypeDefinitionSource definitionSource : definitionSources) {
            if (definitionSource.isMatch(definition)) {
                definitionSource.register(definition);
                break;
            }
        }
    }
}
