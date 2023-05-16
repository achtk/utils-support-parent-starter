package com.chua.common.support.reflection.craft;

import com.chua.common.support.function.Joiner;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.chua.common.support.reflection.marker.MethodBenchFactory.VOID_METHOD_DESCRIBE;


/**
 * 方法
 *
 * @author CH
 */
public class MethodCraftTable implements CraftTable<MethodDescribe> {
    final List<MethodDescribe> cache = new LinkedList<>();
    final AtomicBoolean hasNewDescribe = new AtomicBoolean();

    public MethodCraftTable(Class<?> type) {
        ClassUtils.doWithMethods(type, method -> {
            method.setAccessible(true);
            cache.add(MethodDescribe.of(method));
        });
    }

    /**
     * 添加方法描述
     *
     * @param methodDescribe 方法描述
     */
    @Override
    public void addDescribe(MethodDescribe methodDescribe) {
        hasNewDescribe.set(true);
        cache.add(methodDescribe);
    }

    @Override
    public boolean hasNewDescribe() {
        return hasNewDescribe.get();
    }

    @Override
    public void forEach(Consumer<MethodDescribe> describe) {
        cache.forEach(describe);
    }

    @Override
    public void forCreateEach(Consumer<MethodDescribe> describe) {
        for (MethodDescribe methodDescribe : cache) {
            if (methodDescribe.isCreate()) {
                describe.accept(methodDescribe);
            }
        }
    }

    /**
     * 获取方法描述
     *
     * @param name           名称
     * @param parameterTypes 字段类型
     * @return 描述
     */
    public MethodDescribe get(String name, String[] parameterTypes) {
        if (StringUtils.isNullOrEmpty(name)) {
            return VOID_METHOD_DESCRIBE;
        }

        for (MethodDescribe methodDescribe : cache) {
            if (methodDescribe.isMatch(name, parameterTypes)) {
                return methodDescribe;
            }
        }

        return VOID_METHOD_DESCRIBE;
    }

    /**
     * 获取方法描述
     *
     * @param name 名称
     * @return 描述
     */
    public MethodDescribe get(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return VOID_METHOD_DESCRIBE;
        }

        for (MethodDescribe methodDescribe : cache) {
            if (methodDescribe.isMatch(name)) {
                return methodDescribe;
            }
        }

        return VOID_METHOD_DESCRIBE;
    }

    /**
     * 是否存在方法被修改
     *
     * @return 是否存在方法被修改
     */
    public boolean methodModify() {
        for (MethodDescribe methodDescribe : cache) {
            if (methodDescribe.modify()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 集合
     *
     * @return 集合
     */
    public Map<String, Method> asMap() {
        Map<String, Method> rs = new HashMap<>(cache.size());
        for (MethodDescribe describe : cache) {
            rs.put(describe.name() + Joiner.on("#").join(describe.parameterTypes()), describe.method());
        }
        return rs;
    }


    /**
     * 方法包含注解
     *
     * @param annotationType 注解
     * @return 方法包含注解
     */
    public boolean hasMethodAnnotation(Class<? extends Annotation> annotationType) {
        for (MethodDescribe methodDescribe : cache) {
            if (methodDescribe.hasAnnotation(annotationType)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 方法包含参数
     *
     * @param type 参数
     * @return 方法包含注解
     */
    public boolean hasMethodByParameterType(Class<?>... type) {
        for (MethodDescribe methodDescribe : cache) {
            if (methodDescribe.hasMethodByParameterType(type)) {
                return true;
            }
        }

        return false;
    }
}
