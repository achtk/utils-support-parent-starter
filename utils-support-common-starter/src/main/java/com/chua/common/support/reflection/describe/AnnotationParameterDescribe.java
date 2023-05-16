package com.chua.common.support.reflection.describe;

import com.chua.common.support.utils.ObjectUtils;
import lombok.Data;

import java.util.Objects;

/**
 * 注解参数值
 *
 * @author CH
 */
@Data
public class AnnotationParameterDescribe {
    /**
     * 注解名称
     */
    private String name;
    /**
     * 类型
     */
    private String type;
    /**
     * 值
     */
    private Object value;

    /**
     * 注解参数值
     *
     * @param k 参数名称
     * @param v 值
     * @return 结果
     */
    public static AnnotationParameterDescribe of(String k, Object v) {
        AnnotationParameterDescribe item = new AnnotationParameterDescribe();
        item.setName(k);
        item.setValue(v);

        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (name.equals(o)) {
            return true;
        }
        AnnotationParameterDescribe that = (AnnotationParameterDescribe) o;
        return ObjectUtils.equal(name, that.name) && ObjectUtils.equal(type, that.type) && ObjectUtils.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.hashCode(name, type, value);
    }

    /**
     * 是否相等
     *
     * @param value 值
     * @return 是否相等
     */
    public boolean isEquals(String value) {
        return name.equals(value);
    }
}
