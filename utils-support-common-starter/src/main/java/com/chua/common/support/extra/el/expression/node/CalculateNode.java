package com.chua.common.support.extra.el.expression.node;

import com.chua.common.support.extra.el.expression.token.Token;

import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public interface CalculateNode
{

    Object calculate(Map<String, Object> variables);

    Token token();

    String literals();
}
