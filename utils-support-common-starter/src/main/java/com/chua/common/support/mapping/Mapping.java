package com.chua.common.support.mapping;

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
        return of(beanType, MappingConfig.DEFAULT);
    }

    /**
     * 初始化
     *
     * @param beanType      类型
     * @param mappingConfig 映射配置
     * @return 结果
     */
    static <T>Mapping<T> of(Class<T> beanType, MappingConfig mappingConfig) {
        return new HttpMapping<>(beanType, mappingConfig);
    }
    /**
     * 获取
     *
     * @return {@link T}
     */
    T get();
}
