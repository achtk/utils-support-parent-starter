package com.chua.common.support.reflection.dynamic;

import com.chua.common.support.reflection.dynamic.attribute.AnnotationAttribute;
import com.chua.common.support.reflection.dynamic.attribute.ConstructAttribute;
import com.chua.common.support.reflection.dynamic.attribute.FieldAttribute;
import com.chua.common.support.reflection.dynamic.attribute.MethodAttribute;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * 构造器
 *
 * @author ch
 */
public abstract class AbstractDynamicBeanStandardBuilder<T> implements DynamicBeanBuilder<T> {

    protected String name = this.getClass().getTypeName() + "$Adaptor";
    protected String superType;
    protected final List<String> packages = new LinkedList<>();
    protected final List<String> interfaces = new LinkedList<>();
    protected final List<FieldAttribute> fieldsInfos = new LinkedList<>();
    protected final List<MethodAttribute> methodInfos = new LinkedList<>();
    protected final List<ConstructAttribute> constructInfos = new LinkedList<>();
    protected BiFunction<Class<?>, String, Map<String, Object>> annotationFunction;
    protected boolean isClass = true;
    protected List<AnnotationAttribute> annotations = new LinkedList<>();
    protected String source;

    @Override
    public DynamicBeanBuilder<T> isClass() {
        this.isClass = true;
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> source(String source) {
        this.source = source;
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> annotations(AnnotationAttribute... annotations) {
        this.annotations.addAll(Arrays.asList(annotations));
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> packages(String... name) {
        this.packages.addAll(Arrays.asList(name));
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> setInterfaces(String... name) {
        interfaces.clear();
        return interfaces(name);
    }

    @Override
    public DynamicBeanBuilder<T> interfaces(String... name) {
        interfaces.addAll(Arrays.asList(name));
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> superType(String name) {
        this.superType = name;
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> field(FieldAttribute attribute) {
        fieldsInfos.add(attribute);
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> constructor(ConstructAttribute attribute) {
        constructInfos.add(attribute);
        return this;
    }

    @Override
    public DynamicBeanBuilder<T> method(MethodAttribute attribute) {
        methodInfos.add(attribute);
        return this;
    }



}
