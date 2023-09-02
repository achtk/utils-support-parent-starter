package com.chua.common.support.objects.source;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.definition.ClassTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
public abstract class AbstractTypeDefinitionSource implements TypeDefinitionSource {

    protected ConfigureContextConfiguration configuration;
    protected Map<String, SortedList<TypeDefinition>> nameDefinitions = new ConcurrentHashMap<>();
    protected Map<String, SortedList<TypeDefinition>> typeDefinitions = new ConcurrentHashMap<>();

    private static final Comparator<TypeDefinition> COMPARABLE = Comparator.comparingInt(TypeDefinition::order);

    public AbstractTypeDefinitionSource(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
    }

    protected void register(Object o) {
        TypeDefinition typeDefinition = new ClassTypeDefinition(ClassUtils.toType(o));
        String name = typeDefinition.getName();
        if (StringUtils.isNotEmpty(name)) {
            nameDefinitions.computeIfAbsent(name, it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
        }
        typeDefinitions.computeIfAbsent(typeDefinition.getType().getTypeName(), it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
        Set<String> strings = typeDefinition.superTypeAndInterface();
        for (String string : strings) {
            typeDefinitions.computeIfAbsent(string, it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
        }
    }
}
