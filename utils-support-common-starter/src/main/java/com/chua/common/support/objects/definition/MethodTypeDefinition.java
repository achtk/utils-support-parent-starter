package com.chua.common.support.objects.definition;

import com.chua.common.support.objects.definition.element.AnnotationDefinition;
import com.chua.common.support.objects.definition.element.FieldDefinition;
import com.chua.common.support.objects.definition.element.MethodDefinition;
import com.chua.common.support.objects.definition.resolver.AnnotationResolver;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import com.chua.common.support.spi.ServiceProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * 定义
 *
 * @author CH
 */
public class MethodTypeDefinition implements TypeDefinition {

    private final Method method;
    private Map<String, AnnotationDefinition> annotationDefinitions;

    private final Map<String, List<MethodDefinition>> listMap = new HashMap<>(1);
    private List<Annotation> mapping = new LinkedList<>();
    private int order;

    public MethodTypeDefinition(Class<?> type, Method method) {
        this.method = method;
        this.listMap.put(method.getName(), Collections.singletonList(new MethodDefinition(method)));
        this.annotationDefinitions = ServiceProvider.of(AnnotationResolver.class).getSpiService().get(method);
    }

    public MethodTypeDefinition(Method method) {
        this(method.getDeclaringClass(), method);
    }

    @Override
    public Class<?> getType() {
        return method.getReturnType();
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public int order() {
        return order;
    }

    @Override
    public int order(int order) {
        this.order = order;
        return order;
    }

    @Override
    public boolean isAssignableFrom(Class<?> target) {
        return false;
    }

    @Override
    public boolean fromAssignableFrom(Class<?> target) {
        return false;
    }

    @Override
    public ClassLoader getClassLoader() {
        return method.getClass().getClassLoader();
    }

    @Override
    public Set<String> superTypeAndInterface() {
        return Collections.emptySet();
    }

    @Override
    public String[] getName() {
        return new String[0];
    }

    @Override
    public <T> T newInstance(TypeDefinitionSourceFactory typeDefinitionSourceFactory) {
        return null;
    }

    @Override
    public List<URL> getDepends() {
        return Collections.emptyList();
    }

    @Override
    public void addBeanName(String[] value) {

    }

    @Override
    public Map<String, List<MethodDefinition>> getMethodDefinition() {
        return listMap;
    }

    @Override
    public List<FieldDefinition> getFieldDefinition() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return annotationDefinitions.containsKey(annotationType.getTypeName());
    }

    @Override
    public List<AnnotationDefinition> getAnnotationDefinition() {
        return new ArrayList<>(annotationDefinitions.values());
    }

    public <T extends Annotation> void addAnnotation(T mapping) {
        this.mapping.add(mapping);
    }
}
