package com.chua.common.support.collection;


/**
 * @author code4crafter@gmail.com
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 多键映射基础
 * multi-key map, some basic objects *
 *
 * @author yihua.huang
 * @date 2023/08/31
 */
public abstract class BaseMultiKeyMapBase {

    protected static final Class<? extends Map> DEFAULT_CLAZZ = HashMap.class;
    @SuppressWarnings("rawtypes")
    private Class<? extends Map> protoMapClass = DEFAULT_CLAZZ;

    public BaseMultiKeyMapBase() {
    }

    @SuppressWarnings("rawtypes")
    public BaseMultiKeyMapBase(Class<? extends Map> protoMapClass) {
        this.protoMapClass = protoMapClass;
    }

    @SuppressWarnings("unchecked")
    protected <K, V2> Map<K, V2> newMap() {
        try {
            return (Map<K, V2>) protoMapClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("wrong proto type map "
                    + protoMapClass);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("wrong proto type map "
                    + protoMapClass);
        }
    }
}