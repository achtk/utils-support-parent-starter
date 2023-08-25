package com.chua.common.support.extra.el.expression.node.impl;

import com.chua.common.support.constant.ConstantType;
import com.chua.common.support.extra.el.expression.node.CalculateNode;

import java.lang.reflect.Method;
import java.util.Map;

public class ReflectMethodNode extends AbstractMethodNode
{
    protected final  boolean       recognizeEveryTime;
    private final    CalculateNode beanNode;
    private final    String        methodName;
    private volatile Method        method;
    private volatile Class<?>      beanType;
    private          ConstantType[] convertTypes;

    public ReflectMethodNode(String literals, CalculateNode beanNode, boolean recognizeEveryTime)
    {
        methodName = literals;
        this.beanNode = beanNode;
        this.recognizeEveryTime = recognizeEveryTime;
    }

    @Override
    public Object calculate(Map<String, Object> variables)
    {
        Object value = beanNode.calculate(variables);
        if (value == null)
        {
            return value;
        }
        Object[] args = new Object[argsNodes.length];
        try
        {
            for (int i = 0; i < args.length; i++)
            {
                args[i] = argsNodes[i].calculate(variables);
            }
            Method invoke = getMethod(value, args);
            MethodNodeUtil.convertArgs(args, convertTypes);
            return invoke.invoke(value, args);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Method getMethod(Object value, Object[] args)
    {
        if (recognizeEveryTime)
        {
            Method accessMethod = method;
            if (accessMethod == null || beanType.isAssignableFrom(value.getClass()) == false)
            {
                synchronized (this)
                {
                    if ((accessMethod = method) == null || beanType.isAssignableFrom(value.getClass()) == false)
                    {
                        nextmethod:
                        for (Method each : value.getClass().getMethods())
                        {
                            if (each.getName().equals(methodName) && each.getParameterTypes().length == args.length)
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
                                    else
                                    {
                                        if (args[i] != null && parameterTypes[i].isAssignableFrom(args[i].getClass()) == false)
                                        {
                                            continue nextmethod;
                                        }
                                    }
                                }
                                convertTypes = MethodNodeUtil.buildConvertTypes(parameterTypes);
                                accessMethod = each;
                                accessMethod.setAccessible(true);
                                beanType = value.getClass();
                                method = accessMethod;
                                return accessMethod;
                            }
                        }
                        throw new NullPointerException();
                    }
                }
            }
            return method;
        }
        else
        {
            if (method == null)
            {
                synchronized (this)
                {
                    if (method == null)
                    {
                        Class<?> ckass = value.getClass();
                        nextmethod:
                        for (Method each : ckass.getMethods())
                        {
                            if (each.getName().equals(methodName) && each.getParameterTypes().length == args.length)
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
                    throw new NullPointerException();
                }
            }
            return method;
        }
    }

    @Override
    public String literals()
    {
        StringBuilder cache = new StringBuilder();
        cache.append(beanNode.literals()).append('.').append(methodName).append('(');
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
