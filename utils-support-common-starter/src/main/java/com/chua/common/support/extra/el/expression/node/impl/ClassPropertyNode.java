package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.lang.reflect.Field;
import java.util.Map;
/**
 * 基础类
 * @author CH
 */
public class ClassPropertyNode implements CalculateNode
{
    protected final Class<?> beanType;
    protected final Field    field;

    /**
     * 使用通过变量名和属性名访问该变量的属性
     *
     * @param literals
     */
    public ClassPropertyNode(String literals, CalculateNode beanNode)
    {
        try
        {
            beanType = (Class<?>) beanNode.calculate(null);
            field = beanType.getField(literals);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("类型的静态属性无法获取到,检查" + literals, e);
        }
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        try
        {
            return field.get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Token token()
    {
        return ValueResult.PROPERTY;
    }

    @Override
    public String toString()
    {
        return literals();
    }

    @Override
    public String literals()
    {
        return beanType.getName() + "." + field.getName();
    }
}
