package com.chua.common.support.reflection.craft;


import com.chua.common.support.reflection.describe.ConstructDescribe;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 构造
 *
 * @author CH
 */
public class ConstructCraftTable implements CraftTable<ConstructDescribe> {
    final List<ConstructDescribe> cache = new LinkedList<>();
    final AtomicBoolean hasNewDescribe = new AtomicBoolean();

    static final ConstructDescribe VOID_CONSTRUCT_DESCRIBE = new ConstructDescribe.VoidConstructDescribe();

    public ConstructCraftTable(Class<?> type) {
        Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        for (Constructor<?> declaredConstructor : declaredConstructors) {
            declaredConstructor.setAccessible(true);
            cache.add(ConstructDescribe.of(declaredConstructor));
        }
        ;
    }

    /**
     * 添加方法描述
     *
     * @param constructDescribe 方法描述
     */
    @Override
    public void addDescribe(ConstructDescribe constructDescribe) {
        hasNewDescribe.set(true);
        cache.add(constructDescribe);
    }

    @Override
    public boolean hasNewDescribe() {
        return hasNewDescribe.get();
    }

    @Override
    public void forEach(Consumer<ConstructDescribe> describe) {
        cache.forEach(describe);
    }

    @Override
    public void forCreateEach(Consumer<ConstructDescribe> describe) {

    }

    /**
     * 获取方法描述
     *
     * @param name           名称
     * @param parameterTypes 字段类型
     * @return 描述
     */
    public ConstructDescribe get(String name, String[] parameterTypes) {
        if (StringUtils.isNullOrEmpty(name)) {
            return VOID_CONSTRUCT_DESCRIBE;
        }

        for (ConstructDescribe constructDescribe : cache) {
            if (constructDescribe.isMatch(name, parameterTypes)) {
                return constructDescribe;
            }
        }

        return VOID_CONSTRUCT_DESCRIBE;
    }

    /**
     * 获取方法描述
     *
     * @param name 名称
     * @return 描述
     */
    public ConstructDescribe get(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            return VOID_CONSTRUCT_DESCRIBE;
        }

        for (ConstructDescribe constructDescribe : cache) {
            if (constructDescribe.isMatch(name)) {
                return constructDescribe;
            }
        }

        return VOID_CONSTRUCT_DESCRIBE;
    }

    /**
     * 是否存在方法被修改
     *
     * @return 是否存在方法被修改
     */
    public boolean methodModify() {
        for (ConstructDescribe constructDescribe : cache) {
            if (constructDescribe.modify()) {
                return true;
            }
        }
        return false;
    }

}
