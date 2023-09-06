package com.chua.common.support.lang.expression.parser;

import com.chua.common.support.constant.RegexConstant;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.value.Value;

import java.util.Map;
import java.util.regex.Matcher;


/**
 * 解析器
 * @author CH
 */
public interface ExpressionParser {
    /**
     * 创建解析器
     * @return 解析器
     */
    static ExpressionParser create() {
        return ServiceProvider.of(ExpressionParser.class).getNewExtension("el");
    }

    /**
     * 设置参数
     * @param name 名称
     * @param value 值
     * @return this
     */

    ExpressionParser setVariable(String name, Object value);
    /**
     * 设置参数
     * @param value 值
     * @return this
     */

    default ExpressionParser setVariable(Map<String, Object> value) {
        value.forEach(this::setVariable);
        return this;
    }

    /**
     * 解析表达式
     * @param express 表达式
     * @return 结果
     */
    default Value<?> parse(String express) {
        Matcher matcher = RegexConstant.PLACEHOLDER.matcher(express);
        StringBuilder newPart = new StringBuilder();
        int endOffset = -1;
        while (matcher.find()) {
            String group = matcher.group(2);
            newPart.append(matcher.group(1)).append(parseExpression(group).getValue()).append(matcher.group(3));
            endOffset = matcher.end();
        }

        if(endOffset < express.length()) {
            newPart.append(express.substring(endOffset == -1 ? 0: endOffset));
        }

        return Value.of(newPart.toString());
    }

    /**
     * 解析表达式
     * @param express 表达式
     * @return 结果
     */
    Value<?> parseExpression(String express);
}
