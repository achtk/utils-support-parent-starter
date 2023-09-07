package com.chua.common.support.mapping;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.objects.ConfigureObjectContext;
import lombok.Data;

/**
 * 绑定器
 * @author CH
 */
@Data
public class MappingBinder {

    private ConfigureObjectContext configureObjectContext ;


    /**
     * 绑定
     *
     * @param name 名称
     * @param bean bean
     * @param type 类型
     */
    public void bind(String name, Class<?> type ,Object bean) {
        ExpressionParser expressionParser = configureObjectContext.getExpressionParser();
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
        return configureObjectContext.getExpressionParser().parseExpression(value).getValue();
    }
}
