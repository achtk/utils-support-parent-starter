package com.chua.common.support.mapping;

/**
 * 实体映射
 * @author CH
 */
public abstract class AbstractMapping<T> implements Mapping<T>{

    protected final Class<T> beanType;
    protected final MappingConfig mappingConfig;

    public AbstractMapping(Class<T> beanType, MappingConfig mappingConfig) {
        this.beanType = beanType;
        this.mappingConfig = mappingConfig;
    }
}
