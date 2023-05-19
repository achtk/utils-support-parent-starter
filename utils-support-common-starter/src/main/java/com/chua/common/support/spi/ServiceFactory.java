package com.chua.common.support.spi;

import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Type;

/**
 * 服务工厂
 * @author CH
 */
public interface ServiceFactory<E> {

    /**
     * 获取实现
     * @param name 名称
     * @return 实现
     */
    default E getExtension(String name) {
        Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(this.getClass());
        return ServiceProvider.of((Class<E>) actualTypeArguments[0]).getExtension(name);
    }

    /**
     * 获取实现
     * @param name 名称
     * @return 实现
     */
    default E getNewExtension(String name, Object... args) {
        Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(this.getClass());
        return ServiceProvider.of((Class<E>) actualTypeArguments[0]).getNewExtension(name, args);
    }
}
