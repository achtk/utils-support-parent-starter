package com.chua.common.support.objects.source;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.definition.ObjectTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.spi.ServiceProvider;

import java.lang.annotation.Annotation;
import java.util.Map;

import static com.chua.common.support.objects.source.AbstractTypeDefinitionSource.COMPARABLE;

/**
 * spi
 * @author CH
 */
public class SpiTypeDefinitionSource implements TypeDefinitionSource{
    @Override
    public boolean isMatch(TypeDefinition typeDefinition) {
        return false;
    }

    @Override
    public SortedList<TypeDefinition> getBean(String name, Class<?> targetType) {
        Object extension = ServiceProvider.of(targetType).getExtension(name);
        if(null != extension) {
            return new SortedArrayList<>(new ObjectTypeDefinition(name, extension), COMPARABLE);
        }
        return SortedList.emptyList();
    }

    @Override
    public SortedList<TypeDefinition> getBean(String name) {
        return SortedList.emptyList();
    }

    @Override
    public SortedList<TypeDefinition> getBean(Class<?> targetType) {
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        Map<String, ?> list = ServiceProvider.of(targetType).list();
        for (Map.Entry<String, ?> entry : list.entrySet()) {
            rs.add(new ObjectTypeDefinition(entry.getKey(), entry.getValue()));
        }
        return rs;
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {

    }

    @Override
    public void unregister(String name) {

    }

    @Override
    public void register(TypeDefinition definition) {

    }

    @Override
    public SortedList<TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType) {
        return SortedList.emptyList();
    }
}
