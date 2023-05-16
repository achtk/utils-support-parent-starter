package com.chua.common.support.reflection.marker;

import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.proxy.VoidMethodIntercept;
import com.chua.common.support.reflection.describe.AnnotationDescribe;
import com.chua.common.support.reflection.describe.ConstructDescribe;
import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.JavassistUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.LinkedList;
import java.util.List;

/**
 * 更新
 *
 * @author CH
 */
public class UpdateMarker implements Marker {

    private final Class<?> type;
    private final List<MethodDescribe> methodDescribes = new LinkedList<>();
    private final Marker marker;

    public UpdateMarker(Class<?> type) {
        this.marker = Marker.of(type);
        this.type = type;
    }

    @Override
    public Class<?>[] findAllClassesThatExtendsOrImplements() {
        return new Class[0];
    }

    @Override
    public Class<?> getType() {
        return type;
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
        return marker.createBench(name, parameterTypes);
    }

    @Override
    public Bench createAttributeBench(String name) {
        return marker.createAttributeBench(name);
    }

    @Override
    public Marker annotationType(AnnotationDescribe... annotationDescribes) {
        return marker.annotationType(annotationDescribes);
    }

    @Override
    public Marker imports(String... packages) {
        return marker.imports(packages);
    }

    @Override
    public Marker interfaces(Class<?>... interfaces) {
        return marker.interfaces(interfaces);
    }

    @Override
    public Marker superType(Class<?> superType) {
        return marker.superType(superType);
    }

    @Override
    public Marker name(String name) {
        return marker.name(name);
    }

    @Override
    public Marker create(MethodDescribe methodDescribe) {
        this.methodDescribes.add(methodDescribe);
        marker.create(methodDescribe);
        return this;
    }

    @Override
    public Marker create(FieldDescribe fieldDescribe) {
        marker.create(fieldDescribe);
        return this;
    }

    @Override
    public <T> T marker(Class<T> target) {
        try {
            return createMarker();
        } catch (Exception e) {
            return ProxyUtils.newProxy(target, new VoidMethodIntercept<>());
        }
    }

    @Override
    public Marker ofMarker() {
        return this;
    }

    /**
     * 修改对象
     *
     * @param <T> 类型
     * @return 对象
     */
    @SuppressWarnings("ALL")
    private <T> T createMarker() throws Exception {
        ClassPool classPool = JavassistUtils.getClassPool();
        CtClass ctClass = classPool.get(type.getTypeName());

        for (MethodDescribe methodDescribe : methodDescribes) {
            if (methodDescribe.modify()) {
                CtMethod declaredMethod = ctClass.getDeclaredMethod(methodDescribe.name(), JavassistUtils.toCtClass(methodDescribe.parameterTypes()));
                declaredMethod.setBody(methodDescribe.body());
            }
        }

        Object entity = JavassistUtils.toEntity(ctClass, classPool);
        if (null == entity) {
            return (T) ProxyUtils.newProxy(type, ClassUtils.getDefaultClassLoader(), new VoidMethodIntercept<>());
        }
        return (T) entity;
    }
}
