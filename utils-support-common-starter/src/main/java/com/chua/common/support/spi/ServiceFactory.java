package com.chua.common.support.spi;

import com.chua.common.support.reflection.describe.GenericDescribe;
import com.chua.common.support.reflection.describe.TypeDescribe;

import java.util.Map;

/**
 * 服务工厂
 * @author CH
 */
@SuppressWarnings("ALL")
public interface ServiceFactory<E> {

    /**
     * 获取实现
     * @param name 名称
     * @return 实现
     */
    default E getExtension(String name) {
        TypeDescribe typeDescribe = new TypeDescribe(this);
        GenericDescribe genericDescribe = typeDescribe.getActualTypeArguments();
        return (E) ServiceProvider.of(genericDescribe.get(0).getActualTypeArguments()).getExtension(name);
    }

    /**
     * 获取实现
     * @param name 名称
     * @return 实现
     */
    default E getNewExtension(String name, Object... args) {
        TypeDescribe typeDescribe = new TypeDescribe(this);
        GenericDescribe genericDescribe = typeDescribe.getActualTypeArguments();
        return (E) ServiceProvider.of(genericDescribe.get(0).getActualTypeArguments()).getNewExtension(name, args);
    }

    /**
     * 获取实现
     *
     * @return 实现
     */
    default Map<String, Class<E>> listType() {
        TypeDescribe typeDescribe = new TypeDescribe(this);
        GenericDescribe genericDescribe = typeDescribe.getActualTypeArguments();
        Map stringClassMap = ServiceProvider.of(genericDescribe.get(0).getActualTypeArguments()).listType();
        return stringClassMap;
    }
    /**
     * 获取实现
     *
     * @param args 参数
     * @return 实现
     */
    default Map<String, E> list(Object... args) {
        TypeDescribe typeDescribe = new TypeDescribe(this);
        GenericDescribe genericDescribe = typeDescribe.getActualTypeArguments();
        return (Map<String, E>) ServiceProvider.of(genericDescribe.get(0).getActualTypeArguments()).list(args);

    }
}
