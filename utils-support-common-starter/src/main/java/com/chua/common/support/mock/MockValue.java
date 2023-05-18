package com.chua.common.support.mock;

import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;

/**
 * 值
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
public class MockValue {

    private final Class<?> returnType;
    /**
     * 数据类型
     *
     */
    Mock.Type[] value;

    /**
     * 符号
     */
    Mock.Symbol symbol;

    /**
     * 基础数据
     */
    String base;

    /**
     * 数据格式
     */
    String formatter;

    public MockValue(Mock mock, Field field) {
        this.value = mock.value();
        this.symbol = mock.symbol();
        this.base = mock.base();
        this.returnType = field.getType();
        this.formatter = mock.formatter();
    }
}
