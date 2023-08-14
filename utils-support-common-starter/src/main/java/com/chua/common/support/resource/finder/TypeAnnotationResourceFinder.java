package com.chua.common.support.resource.finder;

import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.ClassResource;
import com.chua.common.support.resource.resource.Resource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.chua.common.support.reflection.reflections.scanners.Scanners.TypesAnnotated;


/**
 * 方法注解查询器
 * @author CH
 */
public class TypeAnnotationResourceFinder extends SubtypeResourceFinder {

    public TypeAnnotationResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        Reflections reflections = SubtypeResourceFinder.getReflections(configuration, classLoader, TypesAnnotated, name);
        Set<Resource> resources = new LinkedHashSet<>();
        Collection<Set<String>> values = reflections.getStore().get(TypesAnnotated.name()).values();
        for (Set<String> value : values) {
            value.forEach(k -> {
                resources.add(new ClassResource(classLoader, k));
            });
        }

        return resources;
    }
}
