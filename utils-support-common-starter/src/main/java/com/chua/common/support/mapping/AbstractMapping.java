package com.chua.common.support.mapping;

/**
 * 实体映射
 * @author CH
 */
public abstract class AbstractMapping<T> implements Mapping<T>{

    protected final Class<T> beanType;
    protected final MappingConfig mappingConfig;
    protected final MappingBinder mappingBinder;

    public AbstractMapping(Class<T> beanType, MappingConfig mappingConfig, MappingBinder mappingBinder) {
        this.beanType = beanType;
        this.mappingConfig = mappingConfig;
        this.mappingBinder = mappingBinder;
    }
}
