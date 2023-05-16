package com.chua.common.support.reflection.marker;

import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.reflection.describe.AnnotationDescribe;
import com.chua.common.support.reflection.describe.ConstructDescribe;
import com.chua.common.support.reflection.describe.FieldDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.EMPTY_ARRAY;

/**
 * 制作器
 *
 * @author CH
 */
public interface Marker {

    Map<Class<?>, Marker> CACHE = new ConcurrentHashMap<>();

    /**
     * 初始化
     *
     * @param obj  对象
     * @param args 参数
     * @return marker
     */
    static Marker of(Object obj, Object... args) {
        if (null == obj) {
            return new NullMarker();
        }

        if (obj instanceof Class) {
            Class<?> type = (Class<?>) obj;
            return TearMarker.of(type, Reflect.create(type).getObjectValue(args).getValue());
        }
        return TearMarker.of(obj.getClass(), obj);
    }

    /**
     * 初始化
     *
     * @param entity 实体
     * @return marker
     */
    static Marker append(Object entity) {
        return new AppendMarker(entity);
    }

    /**
     * 初始化
     *
     * @param entity 实体
     * @return marker
     */
    static Marker create(Object entity) {
        return new CreateMarker(entity);
    }

    /**
     * 初始化
     *
     * @return marker
     */
    static Marker create() {
        return new CreateMarker(null);
    }

    /**
     * 初始化
     *
     * @param target 类型
     * @param <T>    类型
     * @return marker
     */
    static <T> Marker update(Class<T> target) {
        return new UpdateMarker(target);
    }

    /**
     * 创建代理
     *
     * @param type 类型
     * @param args 参数
     * @param <T>  类型
     * @return 对象
     */
    static <T> T proxy(Class<T> type, Object... args) {
        Marker marker = of(type, args);
        return ProxyUtils.newProxy(type, new DelegateMethodIntercept<>(type, it -> {
            Bench bench = marker.createBench(MethodDescribe.builder().method(it.getMethod()).build());
            return bench.executeBean(it.getProxy(), it.getArgs()).getValue();
        }));
    }

    /**
     * 创建代理
     *
     * @param type 类型
     * @param obj  代理对象
     * @param <T>  类型
     * @return 对象
     */
    static <T> T proxy(Class<T> type, Object obj) {
        Marker marker = of(type);
        return ProxyUtils.newProxy(type, new DelegateMethodIntercept<>(type, it -> {
            Bench bench = marker.createBench(MethodDescribe.builder().method(it.getMethod()).build());
            return bench.executeBean(obj, it.getArgs()).getValue();
        }));
    }

    /**
     * 查找子类或者扩展类
     *
     * @return 子类
     */
    Class<?>[] findAllClassesThatExtendsOrImplements();

    /**
     * 获取类型
     *
     * @return 类型
     */
    Class<?> getType();

    /**
     * 获取执行器
     *
     * @param methodDescribe 描述
     * @return 执行
     */
    Bench createBench(MethodDescribe methodDescribe);

    /**
     * 获取执行器
     *
     * @param constructDescribe 描述
     * @return 执行
     */
    Bench createBench(ConstructDescribe constructDescribe);

    /**
     * 获取执行器
     *
     * @param fieldDescribe 描述
     * @return 执行
     */
    Bench createBench(FieldDescribe fieldDescribe);

    /**
     * 获取执行器
     *
     * @param name           名称
     * @param parameterTypes 参数类型
     * @return 执行
     */
    default Bench createBench(String name, String[] parameterTypes) {
        return createBench(MethodDescribe.builder().name(name).parameterTypes(parameterTypes).build());
    }

    /**
     * 获取执行器
     *
     * @param name 名称
     * @return 执行
     */
    default Bench createAttributeBench(String name) {
        return createBench(FieldDescribe.builder().name(name).build());
    }

    /**
     * 获取建造器
     *
     * @param name 名称
     * @return 建造器
     */
    default Bench createCraft(String name) {
        return createBench(name, EMPTY_ARRAY);
    }

    /**
     * 获取建造器
     *
     * @param name           名称
     * @param parameterTypes 参数类型
     * @return 建造器
     */
    default Bench createCraft(String name, Class<?>[] parameterTypes) {
        return createBench(name, ClassUtils.toTypeName(parameterTypes));
    }

    /**
     * 注解
     *
     * @param annotationDescribes 注解
     * @return Marker
     */
    Marker annotationType(AnnotationDescribe... annotationDescribes);

    /**
     * 包
     *
     * @param packages 包
     * @return Marker
     */
    Marker imports(String... packages);

    /**
     * 添加接口
     *
     * @param interfaces 接口
     * @return Marker
     */
    Marker interfaces(Class<?>... interfaces);

    /**
     * 超类
     *
     * @param superType 超类
     * @return Marker
     */
    Marker superType(Class<?> superType);

    /**
     * 名称
     *
     * @param name 名称
     * @return Marker
     */
    Marker name(String name);

    /**
     * 创建方法
     *
     * @param methodDescribe 方法
     * @return Marker
     */
    Marker create(MethodDescribe methodDescribe);

    /**
     * 创建属性
     *
     * @param fieldDescribe 属性
     * @return Marker
     */
    Marker create(FieldDescribe fieldDescribe);

    /**
     * 创建对象
     *
     * @param <T>    类型
     * @param target 目标类型
     * @return 对象
     */
    <T> T marker(Class<T> target);

    /**
     * 创建对象
     *
     * @return 对象
     */
    Marker ofMarker();
}
