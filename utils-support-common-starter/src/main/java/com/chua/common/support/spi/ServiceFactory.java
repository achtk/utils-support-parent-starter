package com.chua.common.support.spi;

import java.util.Map;

/**
 * 服务工厂
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public interface ServiceFactory<E> {
    /**
     * 类型
     * @return 类型
     */
    Class<E> getType();

    /**
     * 获取实现
     * @param name 名称
     * @return 实现
     */
    default E getExtension(String name) {
        return (E) ServiceProvider.of(getType()).getExtension(name);
    }

    /**
     * 获取实现
     * @param name 名称
     * @return 实现
     */
    default E getNewExtension(String name, Object... args) {
        return (E) ServiceProvider.of(getType()).getNewExtension(name, args);
    }

    /**
     * 获取实现
     *
     * @return 实现
     */
    default Map<String, Class<E>> listType() {
        Map stringClassMap = ServiceProvider.of(getType()).listType();
        return stringClassMap;
    }
    /**
     * 获取实现
     *
     * @param args 参数
     * @return 实现
     */
    default Map<String, E> list(Object... args) {
        return (Map<String, E>) ServiceProvider.of(getType()).list(args);

    }
}
