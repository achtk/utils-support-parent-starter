package com.chua.common.support.extra.el.expression.node;

import com.chua.common.support.extra.el.expression.token.Token;

import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public interface CalculateNode
{

    /**
     * 计算
     * @param variables 参数
     * @return 结果
     */
    Object calculate(Map<String, Object> variables);

    /**
     * token
     * @return token
     */
    Token token();

    /**
     * literals
     * @return literals
     */
    String literals();
}
