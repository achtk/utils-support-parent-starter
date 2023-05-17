package com.chua.common.support.value;

import com.chua.common.support.constant.PairType;
import com.chua.common.support.file.export.resolver.ValueResolver;
import com.chua.common.support.utils.ClassUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系
 *
 * @author CH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair {
    /**
     * 字段
     */
    private String name;
    /**
     * 描述
     */
    private String label;
    /**
     * java
     */
    private Class<?> javaType;
    /**
     * jdbc
     */
    private String jdbcType = "VARCHAR";

    /**
     * 对象转换器
     */
    private ValueResolver valueResolver;

    public Pair(String name) {
        this(name, name);
    }

    public Pair(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public Pair(String name, String label, Class<?> javaType) {
        this.name = name;
        this.label = label;
        this.javaType = ClassUtils.fromPrimitive(javaType);
        jdbcType = createJdbcType();
    }

    public Pair(String name, String label, Class<?> javaType, ValueResolver valueResolver) {
        this.name = name;
        this.label = label;
        this.javaType = ClassUtils.fromPrimitive(javaType);
        jdbcType = createJdbcType();
        this.valueResolver = valueResolver;
    }

    private String createJdbcType() {
        PairType[] values = PairType.values();
        for (PairType value : values) {
            if (value.getJavaType() == javaType) {
                return value.getJdbcType();
            }
        }

        return PairType.VARCHAR.getJdbcType();
    }

    /**
     * 转化
     * @param o 值
     * @return 结果
     */
    public Object resolve(Object o) {
        if(null == o || null == valueResolver) {
            return o;
        }

        return valueResolver.resolve(o);
    }
}
