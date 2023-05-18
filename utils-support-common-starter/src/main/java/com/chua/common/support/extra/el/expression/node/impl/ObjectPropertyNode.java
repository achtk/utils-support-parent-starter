package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.extra.el.baseutil.StringUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ReflectUtil;
import com.chua.common.support.extra.el.baseutil.reflect.ValueAccessor;
import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.Token;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.lang.reflect.Field;
import java.util.Map;

public class ObjectPropertyNode implements CalculateNode
{
    private final    CalculateNode beanNode;
    protected        Class<?>      beanType;
    protected        String        propertyName;
    protected        boolean       recognizeEveryTime = true;
    private volatile ValueAccessor valueAccessor;

    /**
     * 使用通过变量名和属性名访问该变量的属性
     *
     * @param literals
     */
    public ObjectPropertyNode(String literals, CalculateNode beanNode, boolean recognizeEveryTime)
    {
        propertyName = literals;
        this.beanNode = beanNode;
        this.recognizeEveryTime = recognizeEveryTime;
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        Object value = beanNode.calculate(variables);
        if (value == null)
        {
            return null;
        }
        try
        {
            return getValueAccessor(value).get(value);
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    @Override
    public Token token()
    {
        return ValueResult.PROPERTY;
    }

    protected final ValueAccessor getValueAccessor(Object value)
    {
        ValueAccessor valueAccessor = this.valueAccessor;
        if (recognizeEveryTime)
        {
            if (valueAccessor == null || beanType.isAssignableFrom(value.getClass()))
            {
                synchronized (this)
                {
                    if ((valueAccessor = this.valueAccessor) == null || beanType.isAssignableFrom(value.getClass()))
                    {
                        return buildValueAccessor(value);
                    }
                }
            }
            return valueAccessor;
        }
        else
        {
            if (valueAccessor == null)
            {
                synchronized (this)
                {
                    if ((valueAccessor = this.valueAccessor) == null)
                    {
                        return buildValueAccessor(value);
                    }
                }
            }
            return valueAccessor;
        }
    }

    private ValueAccessor buildValueAccessor(Object value)
    {
        ValueAccessor valueAccessor;
        Field         propertyField;
        Class<?>      ckass = value.getClass();
        while (ckass != Object.class)
        {
            try
            {
                propertyField = ckass.getDeclaredField(propertyName);
                beanType = value.getClass();
                valueAccessor = this.valueAccessor = new ValueAccessor(propertyField);
                return valueAccessor;
            }
            catch (NoSuchFieldException e)
            {
                ckass = ckass.getSuperclass();
            }
            catch (SecurityException e)
            {
                ReflectUtil.throwException(e);
            }
        }
        throw new NullPointerException(StringUtil.format("无法在类{}中找到属性:{}", value.getClass(), propertyName));
    }

    @Override
    public String literals()
    {
        return beanNode.literals() + "." + propertyName;
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
