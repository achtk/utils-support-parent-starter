package com.chua.common.support.objects.definition;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.classloader.ZipClassLoader;
import com.chua.common.support.objects.definition.element.AnnotationDescribe;
import com.chua.common.support.objects.definition.element.FieldDescribe;
import com.chua.common.support.objects.definition.element.MethodDescribe;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定义
 *
 * @author CH
 */
public class ZipTypeDefinition implements TypeDefinition, InitializingAware {

    protected final File path;
    private final ZipClassLoader zipClassLoader;
    private int order;

    public ZipTypeDefinition(File path) {
        this.path = path;
        this.zipClassLoader = new ZipClassLoader();
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public Class<?> getType() {
        return null;
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
        return zipClassLoader;
    }

    @Override
    public Set<String> superTypeAndInterface() {
        return Collections.emptySet();
    }

    @Override
    public String[] getName() {
        return new String[]{path.getAbsolutePath()};
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
    public Map<String, List<MethodDescribe>> getMethodDefinition() {
        return Collections.emptyMap();
    }

    @Override
    public List<FieldDescribe> getFieldDefinition() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return false;
    }

    @Override
    public List<AnnotationDescribe> getAnnotationDefinition() {
        return Collections.emptyList();
    }
}
