package com.chua.common.support.extra.el.expression.node.impl;

import com.jfirer.baseutil.reflect.ReflectUtil;
import com.chua.common.support.extra.el.expression.node.CalculateNode;
import com.chua.common.support.extra.el.expression.token.ValueResult;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

public class StaticObjectMethodNode extends AbstractMethodNode
{
    private final    Class<?>      beanType;
    private final    String        methodName;
    private volatile Method        method;
    private          ConvertType[] convertTypes;

    public StaticObjectMethodNode(String literals, CalculateNode beanNode)
    {
        if (beanNode.token() != ValueResult.TYPE)
        {
            throw new IllegalArgumentException("静态方法的前面一个节点必须是类型节点");
        }
        beanType = (Class<?>) beanNode.calculate(null);
        methodName = literals;
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        Object[] args = new Object[argsNodes.length];
        try
        {
            for (int i = 0; i < args.length; i++)
            {
                args[i] = argsNodes[i].calculate(variables);
            }
            Method invoke = getMethod(args);
            MethodNodeUtil.convertArgs(args, convertTypes);
            return invoke.invoke(null, args);
        }
        catch (Exception e)
        {
            ReflectUtil.throwException(e);
            return null;
        }
    }

    private Method getMethod(Object[] args)
    {
        if (method == null)
        {
            synchronized (this)
            {
                if (method == null)
                {
                    nextmethod:
                    for (Method each : beanType.getMethods())
                    {
                        if (Modifier.isStatic(each.getModifiers()) && each.getName().equals(methodName) && each.getParameterTypes().length == args.length)
                        {
                            Class<?>[] parameterTypes = each.getParameterTypes();
                            for (int i = 0; i < args.length; i++)
                            {
                                if (parameterTypes[i].isPrimitive())
                                {
                                    if (args[i] == null || MethodNodeUtil.isWrapType(parameterTypes[i], args[i].getClass()) == false)
                                    {
                                        continue nextmethod;
                                    }
                                }
                                else if (args[i] != null && parameterTypes[i].isAssignableFrom(args[i].getClass()) == false)
                                {
                                    continue nextmethod;
                                }
                            }
                            convertTypes = MethodNodeUtil.buildConvertTypes(parameterTypes);
                            each.setAccessible(true);
                            method = each;
                            return method;
                        }
                    }
                }
                throw new NullPointerException("没有在类" + beanType.getName() + "找到静态方法" + methodName);
            }
        }
        return method;
    }

    @Override
    public String literals()
    {
        StringBuilder cache = new StringBuilder();
        cache.append(beanType.getName()).append('.').append(methodName).append('(');
        if (argsNodes != null)
        {
            for (CalculateNode each : argsNodes)
            {
                cache.append(each.literals()).append(',');
            }
            if (cache.charAt(cache.length() - 1) == ',')
            {
                cache.setLength(cache.length() - 1);
            }
        }
        cache.append(')');
        return cache.toString();
    }

    @Override
    public String toString()
    {
        return literals();
    }
}
