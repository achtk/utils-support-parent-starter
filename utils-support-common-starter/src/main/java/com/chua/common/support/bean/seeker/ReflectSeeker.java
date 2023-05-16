package com.chua.common.support.bean.seeker;


import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * reflect
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ReflectSeeker implements Seeker {

    final Reflections reflections;
    private final Set<Class<? extends Annotation>> annotationType = new LinkedHashSet<>();

    public ReflectSeeker(Class<? extends Annotation>... annotationType) {
        this.annotationType.addAll(Arrays.asList(annotationType));
        this.reflections = new Reflections(new ConfigurationBuilder().setParallel(true)
                .addClassLoaders(ClassLoader.getSystemClassLoader())
                .forPackages("")
                .addScanners(Scanners.TypesAnnotated)
                .addScanners(Scanners.SubTypes)
                .addScanners(Scanners.MethodsAnnotated)
        );
    }


    @Override
    public Set<Class<?>> findAll() {
        if (annotationType.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Class<?>> type = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotationClass : annotationType) {
            type.addAll(reflections.getTypesAnnotatedWith(annotationClass));
        }
        return type;
    }

    @Override
    public <E> Set<Class<? extends E>> getSubTypesOf(Class<E> type) {
        return reflections.getSubTypesOf(type);
    }

    @Override
    public Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> type) {
        return reflections.getMethodsAnnotatedWith(type);
    }
}
