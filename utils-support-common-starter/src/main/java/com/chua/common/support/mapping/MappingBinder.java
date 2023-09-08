package com.chua.common.support.mapping;

import com.chua.common.support.lang.expression.parser.DelegateExpressionParser;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import lombok.Data;

/**
 * 绑定器
 * @author CH
 */
@Data
public class MappingBinder {

    private ExpressionParser expressionParser = new DelegateExpressionParser();


    /**
     * 绑定
     *
     * @param name 名称
     * @param bean bean
     * @param type 类型
     */
    public void bind(String name, Class<?> type ,Object bean) {
        expressionParser.addFunction(type, bean);
        expressionParser.setVariable(name, bean);
    }

    /**
     * 作语法分析
     *
     * @param value 价值
     * @return {@link Object}
     */
    public Object parse(String value) {
        return expressionParser.parseExpression(value).getValue();
    }
}
