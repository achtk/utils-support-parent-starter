package com.chua.common.support.mapping;

import com.chua.common.support.mapping.annotations.MappingAddress;
import com.chua.common.support.spi.ServiceProvider;

/**
 * 实体映射
 * @author CH
 */
public interface Mapping<T> {

    /**
     * 初始化
     * @param beanType 类型
     * @return 结果
     * @param <T> 类型
     */
    static <T>Mapping<T> of(Class<T> beanType) {
        return of("http", beanType, MappingConfig.DEFAULT);
    }

    /**
     * 初始化
     *
     * @param beanType      类型
     * @param mappingConfig 映射配置
     * @return 结果
     */
    static <T>Mapping<T> of(Class<T> beanType, MappingConfig mappingConfig) {
        return new HttpMapping<>(beanType, mappingConfig, new MappingBinder());
    }

    /**
     * 属于
     * 初始化
     *
     * @param beanType      类型
     * @param mappingConfig 映射配置
     * @param name          名称
     * @return 结果
     */
    @SuppressWarnings("ALL")
    static <T>Mapping<T> of(String name, Class<T> beanType, MappingConfig mappingConfig) {
        return ServiceProvider.of(Mapping.class).getNewExtension(name, beanType, mappingConfig, new MappingBinder());
    }

    /**
     * 汽车
     *
     * @param beanType bean类型
     * @return {@link Mapping}<{@link T}>
     */
    static  <T>Mapping<T> auto(Class<T> beanType) {
        MappingAddress mappingAddress = beanType.getDeclaredAnnotation(MappingAddress.class);
        if(null == mappingAddress) {
            throw new NullPointerException();
        }

        String mappingType = mappingAddress.mappingType();
        return of(mappingType, beanType, MappingConfig.DEFAULT);
    }

    /**
     * 获取
     *
     * @return {@link T}
     */
    T get();
}
