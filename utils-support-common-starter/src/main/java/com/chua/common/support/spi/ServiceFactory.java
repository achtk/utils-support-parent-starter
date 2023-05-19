package com.chua.common.support.spi;

import com.chua.common.support.utils.ClassUtils;

import java.lang.reflect.Type;
import java.util.Map;

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

    /**
     * 获取实现
     *
     * @return 实现
     */
    default Map<String, Class<E>> listType() {
        Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(this.getClass());
        return ServiceProvider.of((Class<E>) actualTypeArguments[0]).listType();

    }
    /**
     * 获取实现
     *
     * @param args 参数
     * @return 实现
     */
    default Map<String, E> list(Object... args) {
        Type[] actualTypeArguments = ClassUtils.getActualTypeArguments(this.getClass());
        return ServiceProvider.of((Class<E>) actualTypeArguments[0]).list();

    }
}
