package com.chua.common.support.reflection.marker;


import com.chua.common.support.describe.describe.AnnotationDescribe;
import com.chua.common.support.describe.describe.ConstructDescribe;
import com.chua.common.support.describe.describe.FieldDescribe;
import com.chua.common.support.describe.describe.MethodDescribe;
import com.chua.common.support.proxy.ProxyUtils;

/**
 * 简单制作器
 *
 * @author CH
 */
final class NullMarker implements Marker {


    @Override
    public Class<?>[] findAllClassesThatExtendsOrImplements() {
        return new Class[0];
    }

    @Override
    public Class<?> getType() {
        return void.class;
    }

    @Override
    public Bench createBench(MethodDescribe methodDescribe) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createBench(ConstructDescribe constructDescribe) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createBench(FieldDescribe fieldDescribe) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createBench(String name, String[] parameterTypes) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Bench createAttributeBench(String name) {
        return VoidBench.INSTANCE;
    }

    @Override
    public Marker annotationType(AnnotationDescribe... annotationDescribes) {
        return this;
    }

    @Override
    public Marker imports(String... packages) {
        return this;
    }

    @Override
    public Marker interfaces(Class<?>... interfaces) {
        return this;
    }

    @Override
    public Marker superType(Class<?> superType) {
        return this;
    }

    @Override
    public Marker name(String name) {
        return this;
    }

    @Override
    public Marker create(MethodDescribe methodDescribe) {
        return this;
    }

    @Override
    public Marker create(FieldDescribe fieldDescribe) {
        return this;
    }

    @Override
    public <T> T marker(Class<T> target) {
        return ProxyUtils.newProxy(target, (obj, method, args, proxy) -> null);
    }

    @Override
    public Marker ofMarker() {
        return Marker.of(null);
    }
}
