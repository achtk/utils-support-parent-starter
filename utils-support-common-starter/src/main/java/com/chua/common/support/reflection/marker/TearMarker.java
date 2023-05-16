package com.chua.common.support.reflection.marker;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.reflection.craft.ConstructCraftTable;
import com.chua.common.support.reflection.craft.FieldCraftTable;
import com.chua.common.support.reflection.craft.MethodCraftTable;
import com.chua.common.support.reflection.describe.AnnotationDescribe;
import com.chua.common.support.reflection.describe.ConstructDescribe;
import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.reflection.reflections.ReflectionUtils;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.resource.ResourceConfiguration;
import com.chua.common.support.resource.finder.SubtypeResourceFinder;
import com.chua.common.support.utils.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.chua.common.support.constant.CommonConstant.*;


/**
 * 简单制作器
 *
 * @author CH
 */
final class TearMarker implements Marker {

    private Class<?> type;

    private Set<Class<?>> subType = new LinkedHashSet<>();

    private boolean subTypeLoaded;
    private Object entity;
    private FieldCraftTable fieldCraftTable;
    private MethodCraftTable methodCraftTable;

    private final ConstructCraftTable constructCraftTable;
    final AtomicBoolean hasNewDescribe = new AtomicBoolean();
    private final List<String> packages = new LinkedList<>();
    private final List<Class<?>> interfaces = new LinkedList<>();
    private Class<?> superType;
    private String name;
    private AnnotationDescribe[] annotationDescribes;

    private static final Map<Class<?>, TearMarker> CACHE = new ConcurrentReferenceHashMap<>(64);

    public TearMarker(Class<?> type, Object entity) {
        this.entity = entity;
        this.type = type;
        this.fieldCraftTable = new FieldCraftTable(type);
        this.methodCraftTable = new MethodCraftTable(type);
        this.constructCraftTable = new ConstructCraftTable(type);
        if (Proxy.isProxyClass(type)) {
            analysisProxy(type);
        }
    }

    /**
     * 初始化
     *
     * @param type   类型
     * @param entity 实体
     * @return 结果
     */
    public static synchronized TearMarker of(Class<?> type, Object entity) {
        TearMarker ifPresent = CACHE.get(type);
        if (null != ifPresent) {
            ifPresent.entity = entity;
            return ifPresent;
        }

        TearMarker tearMarker = new TearMarker(type, entity);
        CACHE.put(type, tearMarker);
        return tearMarker;
    }

    /**
     * 解析代理
     *
     * @param type 代理类
     */
    private void analysisProxy(Class<?> type) {
        String toString = entity.toString();
        if (ClassUtils.isPresent(toString)) {
            this.type = ClassUtils.forName(toString);
            this.fieldCraftTable = new FieldCraftTable(type);
            this.methodCraftTable = new MethodCraftTable(type);
        }

    }

    @Override
    public Class<?>[] findAllClassesThatExtendsOrImplements() {
        if (!subTypeLoaded) {
            synchronized (this) {
                if (!subTypeLoaded) {
                    subTypeLoaded = true;
                    List<URL> list = ClassUtils.classLoaderJarRoots(type.getClassLoader());
                    for (URL url : list) {
                        if (!FILE.equals(url.getProtocol())) {
                            continue;
                        }

                        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                                .setParallel(true)
                                .setExpandSuperTypes(false)
                                .setScanners(Scanners.SubTypes)
                                .setClassLoaders(new ClassLoader[]{getType().getClassLoader()});

                        Set subTypesOf =
                                new Reflections(configurationBuilder.setUrls(url))
                                        .getSubTypesOf(type);
                        subType.addAll(subTypesOf);
                    }
                }
            }
        }
        return subType.toArray(new Class[0]);
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Bench createBench(MethodDescribe methodDescribe) {
        return createBench(methodDescribe.name(), methodDescribe.parameterTypes());
    }

    @Override
    public Bench createBench(ConstructDescribe constructDescribe) {
        return new ConstructBench(constructCraftTable.get(constructDescribe.name(), constructDescribe.parameterTypes()), entity);
    }

    @Override
    public Bench createBench(FieldDescribe fieldDescribe) {
        return createAttributeBench(fieldDescribe.name());
    }

    @Override
    public Bench createBench(String name, String[] parameterTypes) {
        MethodDescribe methodDescribe = methodCraftTable.get(name, parameterTypes);
        if (null == methodDescribe || methodDescribe instanceof MethodBenchFactory.VoidMethodDescribe) {
            methodDescribe = methodCraftTable.get(name);
        }

        if (null == methodDescribe || methodDescribe instanceof MethodBenchFactory.VoidMethodDescribe) {
            methodDescribe = createGetter(name, parameterTypes);
        }
        return new MethodBench(methodDescribe, entity, name);
    }

    /**
     * 创建Getter方法
     *
     * @param name           名称
     * @param parameterTypes 参数类型
     * @return 结果
     */
    private MethodDescribe createGetter(String name, final String[] parameterTypes) {
        name = METHOD_GETTER + Converter.toFirstUpperCase(name);
        MethodDescribe methodDescribe = methodCraftTable.get(name, parameterTypes);
        if (null == methodDescribe || methodDescribe instanceof MethodBenchFactory.VoidMethodDescribe) {
            return methodCraftTable.get(name);
        }

        return methodDescribe;
    }

    @Override
    public Bench createAttributeBench(String name) {
        return new FieldBench(fieldCraftTable.get(name), entity);
    }

    @Override
    public Marker annotationType(AnnotationDescribe... annotationDescribes) {
        this.annotationDescribes = annotationDescribes;
        return this;
    }

    @Override
    public Marker imports(String... packages) {
        hasNewDescribe.set(true);
        this.packages.addAll(Arrays.asList(Optional.ofNullable(packages).orElse(EMPTY_ARRAY)));
        return this;
    }

    @Override
    public Marker interfaces(Class<?>... interfaces) {
        this.interfaces.addAll(Arrays.asList(Optional.ofNullable(interfaces).orElse(EMPTY_CLASS)));
        hasNewDescribe.set(true);
        return this;
    }

    @Override
    public Marker superType(Class<?> superType) {
        this.superType = superType;
        hasNewDescribe.set(true);
        return this;
    }

    @Override
    public Marker name(String name) {
        this.name = name;
        hasNewDescribe.set(true);
        return this;
    }

    @Override
    public Marker create(MethodDescribe methodDescribe) {
        hasNewDescribe.set(true);
        methodDescribe.isCreate(true);
        methodCraftTable.addDescribe(methodDescribe);
        return this;
    }

    @Override
    public Marker create(FieldDescribe fieldDescribe) {
        hasNewDescribe.set(true);
        fieldDescribe.isCreate(true);
        fieldCraftTable.addDescribe(fieldDescribe);
        return this;
    }

    @Override
    public <T> T marker(Class<T> target) {
        if (hasNewDescribe.get()) {
            Marker marker = Marker.create(entity)
                    .imports(packages.toArray(EMPTY_ARRAY))
                    .interfaces(interfaces.toArray(EMPTY_CLASS))
                    .superType(superType)
                    .name(name)
                    .annotationType(annotationDescribes);

            methodCraftTable.forEach(marker::create);
            fieldCraftTable.forEach(marker::create);

            return marker.marker(target);
        }
        //是否存在方法被修改
        if (methodCraftTable.methodModify()) {
            Marker update = Marker.update(target);
            methodCraftTable.forEach(update::create);
            return update.marker(target);
        }
        return Reflect.create(target).getObjectValue().getObject();
    }

    @Override
    public Marker ofMarker() {
        if (hasNewDescribe.get()) {
            Marker marker = null;
            if (!Modifier.isFinal(type.getModifiers())) {
                marker = Marker.append(entity);
                marker.superType(type);
                methodCraftTable.forCreateEach(marker::create);
                fieldCraftTable.forCreateEach(marker::create);
            } else {
                marker = Marker.create(entity);
                marker.create(FieldDescribe.builder().name(CreateMarker.SOURCE).returnType(type).defaultValue(entity).build());
                marker.create(FieldDescribe.builder().name(CreateMarker.SOURCE_MAP).returnType(Map.class).defaultValue(methodCraftTable.asMap()).build());
                methodCraftTable.forEach(marker::create);
                fieldCraftTable.forEach(marker::create);
            }

            marker.imports(packages.toArray(EMPTY_ARRAY))
                    .interfaces(interfaces.toArray(EMPTY_CLASS))
                    .name(name)
                    .annotationType(annotationDescribes);


            return marker.ofMarker();
        }
        //是否存在方法被修改
        if (methodCraftTable.methodModify()) {
            Marker update = Marker.update(type);
            methodCraftTable.forEach(update::create);
            return update.ofMarker();
        }
        return this;
    }

}
