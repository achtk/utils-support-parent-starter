package com.chua.common.support.context.bean;

import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.value.Value;
import lombok.Getter;

/**
 * 对象结果
 *
 * @author CH
 */
@Getter
public class BeanObjectValue {

    public static final BeanObjectValue EMPTY = new BeanObjectValue(Value.of(null), null);
    private final Value<Object> invoke;
    private MethodDescribe describe;

    public BeanObjectValue(Value<Object> invoke, MethodDescribe describe) {
        this.invoke = invoke;
        this.describe = describe;
    }

    /**
     * 是否存在注解
     * @param name 注解
     * @return 是否存在注解
     */
    public boolean hasAnnotation(String name) {
        return describe.hasAnnotation(name);
    }

    public <T>T getAnnotationValue(Class<T> mappingClass) {
        if(null == describe) {
            return null;
        }

        return describe.getAnnotationValue(mappingClass);
    }
}
