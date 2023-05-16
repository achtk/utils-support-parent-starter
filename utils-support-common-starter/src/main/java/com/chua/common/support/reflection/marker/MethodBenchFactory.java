package com.chua.common.support.reflection.marker;

import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 方法
 *
 * @author CH
 */
public class MethodBenchFactory implements BenchFactory<MethodDescribe> {
    public static MethodDescribe VOID_METHOD_DESCRIBE = new VoidMethodDescribe();
    final List<MethodDescribe> cache = new LinkedList<>();
    final AtomicBoolean hasNewDescribe = new AtomicBoolean();

    public MethodBenchFactory(Class<?> type) {
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
     * void
     */
    public static final class VoidMethodDescribe extends MethodDescribe {

    }
}
