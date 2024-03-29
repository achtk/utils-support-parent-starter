package com.chua.common.support.objects.source;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.mapping.Mapping;
import com.chua.common.support.mapping.annotations.MappingAddress;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.definition.ClassTypeDefinition;
import com.chua.common.support.objects.definition.ObjectTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.util.*;
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

    public static final Comparator<TypeDefinition> COMPARABLE = Comparator.comparingInt(TypeDefinition::order);
    protected static final Set<Class<?>> CACHE = new HashSet<>();

    public AbstractTypeDefinitionSource(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
    }

    protected void register(Object o) {
        Class<?> aClass = ClassUtils.toType(o);
        if (CACHE.contains(aClass)) {
            return;
        }
        CACHE.add(aClass);
        TypeDefinition typeDefinition = null;
        if(aClass.isAnnotationPresent(MappingAddress.class)) {
            typeDefinition = new ObjectTypeDefinition(aClass.getTypeName(), Mapping.auto(aClass).get(), aClass);
        } else {
            typeDefinition = new ClassTypeDefinition(aClass);
        }
        register(typeDefinition);
    }

    @Override
    public void register(TypeDefinition typeDefinition) {
        String[] names = typeDefinition.getName();
        for (String name : names) {
            if (StringUtils.isNotEmpty(name)) {
                nameDefinitions.computeIfAbsent(name, it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
            }
        }
        typeDefinitions.computeIfAbsent(typeDefinition.getType().getTypeName(), it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
        Set<String> strings = typeDefinition.superTypeAndInterface();
        for (String string : strings) {
            typeDefinitions.computeIfAbsent(string, it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
        }
    }

    @Override
    public SortedList<TypeDefinition> getBean(String name, Class<?> targetType) {
        SortedList<TypeDefinition> sortedList = nameDefinitions.get(name);
        if(null == sortedList) {
            return SortedList.emptyList();
        }
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        for (TypeDefinition typeDefinition : sortedList) {
            if (typeDefinition.fromAssignableFrom(targetType)) {
                rs.add(typeDefinition);
            }
        }
        return rs;
    }


    @Override
    public SortedList<TypeDefinition> getBean(String name) {
        return nameDefinitions.get(name);
    }

    @Override
    public SortedList<TypeDefinition> getBean(Class<?> targetType) {
        String typeName = targetType.getTypeName();
        return Optional.ofNullable(typeDefinitions.get(typeName)).orElse(SortedList.emptyList());
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {
    }

    @Override
    public void unregister(String name) {
    }

    @Override
    public SortedList<TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType) {
        return SortedList.emptyList();
    }

}
