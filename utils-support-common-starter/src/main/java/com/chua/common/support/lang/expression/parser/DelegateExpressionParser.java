package com.chua.common.support.lang.expression.parser;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.Value;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.EvalMode;
import com.googlecode.aviator.Options;
import com.googlecode.aviator.runtime.JavaMethodReflectionFunctionMissing;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 解析器
 * @author CH
 */
@Spi("el")
@SpiDefault
@Slf4j
public class DelegateExpressionParser implements ExpressionParser{

    final Map<String, Object> context = ImmutableBuilder.<String, Object>builderOfMap().build();

    {
        AviatorEvaluator.setFunctionMissing(JavaMethodReflectionFunctionMissing.getInstance());
        // 创建解释器
        AviatorEvaluatorInstance engine = AviatorEvaluator.newInstance(EvalMode.INTERPRETER);
        // 打开跟踪执行
        engine.setOption(Options.TRACE_EVAL, true);
    }

    @Override
    public ExpressionParser setVariable(String name, Object value) {
        context.put(name, value);
        return this;
    }

    @Override
    public Value<?> parseExpression(String express) {
        return Value.of(AviatorEvaluator.execute(express, context));
    }

    @Override
    public void addFunction(Class<?> type, Object bean) {
        ClassUtils.doWithMethods(type, method ->  {
            AviatorEvaluator.addFunction(new AbstractVariadicFunction() {

                @Override
                public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
                    Object o = env.get(type.getSimpleName());
                    Object[] args1 = new Object[args.length];
                    for (int i = 0; i < args.length; i++) {
                        AviatorObject aviatorObject = args[i];
                        args1[i] = aviatorObject.getValue(env);
                    }

                    Method method1 = ClassUtils.findMethod(type, method.getName(), ClassUtils.toType(args1));
                    if(null == method1) {
                        return null;
                    }
                    try {
                        ClassUtils.setAccessible(method1);
                        return AviatorRuntimeJavaType.valueOf(method1.invoke(o, args1));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        log.error(e.getMessage());
                    }
                    return AviatorRuntimeJavaType.valueOf(null);
                }

                @Override
                public String getName() {
                    return type.getSimpleName() + "." + method.getName();
                }
            });
        });
    }
}
