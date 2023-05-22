package com.chua.common.support.reflection.describe;

import lombok.Data;

/**
 * Generic
 *
 * @author CH
 */
@Data
public class GenericTypeAttribute {

    private final Class<?> type;
    private final String parameterizedType;
    private int level;

    public GenericTypeAttribute(Class<?> type, String parameterizedTypeName, int level) {
        this.type = type;
        this.parameterizedType = parameterizedTypeName;
        this.level = level;
    }

    public String getActualTypeArguments() {
        return parameterizedType;
    }

    public boolean isEquals(String name) {
        return type.getTypeName().equals(name);
    }
}
