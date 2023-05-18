package com.chua.common.support.extra.el.baseutil.bytecode.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CacheableAnnotationContextFactory implements AnnotationContextFactory {
    protected Map<Method, AnnotationContext> methodAnnotationContextStore = new ConcurrentHashMap<>();
    protected Map<String, AnnotationContext> resourceNameAnnotationContextStore = new ConcurrentHashMap<>();
    protected Map<Field, AnnotationContext> fieldAnnotationContextStore = new ConcurrentHashMap<>();

    protected abstract AnnotationContext build(String resourceName, ClassLoader classLoader);

    protected abstract AnnotationContext build(Method method, ClassLoader classLoader);

    @Override
    public AnnotationContext get(Class<?> ckass, ClassLoader classLoader) {
        return get(ckass.getName().replace('.', '/'), classLoader);
    }

    @Override
    public AnnotationContext get(Method method, ClassLoader classLoader) {
        return methodAnnotationContextStore.computeIfAbsent(method, m -> build(m, classLoader));
    }

    @Override
    public AnnotationContext get(String resourceName, ClassLoader classLoader) {
        return resourceNameAnnotationContextStore.computeIfAbsent(resourceName, value -> build(value, classLoader));
    }

    @Override
    public AnnotationContext get(Field field, ClassLoader classLoader) {
        return fieldAnnotationContextStore.computeIfAbsent(field, f -> build(f, classLoader));
    }

    protected abstract AnnotationContext build(Field field, ClassLoader classLoader);

    @Override
    public AnnotationContext get(Class<?> ckass) {
        return get(ckass, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public AnnotationContext get(String resourceName) {
        return get(resourceName, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public AnnotationContext get(Method method) {
        return get(method, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public AnnotationContext get(Field field) {
        return get(field, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public AnnotationContext get(AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Class) {
            return get((Class)annotatedElement);
        } else if (annotatedElement instanceof Method) {
            return get((Method)annotatedElement);
        } else if (annotatedElement instanceof Field) {
            return get((Field)annotatedElement);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
