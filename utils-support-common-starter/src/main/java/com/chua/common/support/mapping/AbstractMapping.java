package com.chua.common.support.mapping;

/**
 * 实体映射
 * @author CH
 */
public class AbstractMapping<T> implements Mapping<T>{

    protected final Class<T> beanType;

    public AbstractMapping(Class<T> beanType) {
        this.beanType = beanType;
    }
}
