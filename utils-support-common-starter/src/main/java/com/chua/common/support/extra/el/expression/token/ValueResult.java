package com.chua.common.support.extra.el.expression.token;

public enum ValueResult implements Token {
    OPERATOR_RESULT,
    BRACKET,
    // 参数变量
    VARIABLE,
    // 属性访问
    PROPERTY,
    // 方法访问
    METHOD,
    // 字符串
    STRING,
    // 数字
    NUMBER,
    TYPE,
    // 是一个枚举类型的class
    TYPE_ENUM,
    // 是一个枚举值
    ENUM,
    CONSTANT,
}
