package com.chua.common.support.resource.finder;

import com.chua.common.support.collection.ConcurrentReferenceTable;
import com.chua.common.support.collection.Table;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.resource.ClassResource;
import com.chua.common.support.resource.resource.Resource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.chua.common.support.reflection.reflections.scanners.Scanners.MethodsAnnotated;


/**
 * 方法注解查询器
 * @author CH
 */
public class MethodAnnotationResourceFinder extends AbstractResourceFinder {

    private static final Table<ClassLoader, String, Reflections> CACHE = new ConcurrentReferenceTable<>(128);

    public MethodAnnotationResourceFinder(ResourceConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Set<Resource> find(String name) {
        Reflections reflections = SubtypeResourceFinder.getReflections(CACHE, configuration, classLoader, MethodsAnnotated, name);
        Set<Resource> resources = new LinkedHashSet<>();
        Collection<Set<String>> values = reflections.getStore().get(MethodsAnnotated.name()).values();
        for (Set<String> value : values) {
            value.forEach(k -> {
                resources.add(new ClassResource(classLoader, k));
            });
        }

        return resources;
    }
}
