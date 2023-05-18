//package com.chua.common.support.lang.expression;
//
//import com.chua.common.support.lang.expression.parser.SpelExpressionParser;
//import com.chua.common.support.unit.name.NamingCase;
//import com.chua.common.support.value.Value;
//import org.springframework.expression.EvaluationContext;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//
//import java.lang.reflect.Method;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * el
// *
// * @author CH
// */
//public class ExpressionParser {
//
//    final SpelExpressionParser parser = new SpelExpressionParser();
//    final EvaluationContext context = new StandardEvaluationContext();
//
//    final AtomicInteger index = new AtomicInteger(0);
//
//    /**
//     * 设置参数
//     *
//     * @param name  名称
//     * @param value 值
//     * @return this
//     */
//    public ExpressionParser setVariable(String name, Object value) {
//        context.setVariable(name, value);
//        context.setVariable("p" + (index.get()), value);
//        return this;
//    }
//
//    /**
//     * 设置参数
//     *
//     * @param conditions 名称/值
//     * @return this
//     */
//    public ExpressionParser setVariable(Map<String, Object> conditions) {
//        conditions.forEach(context::setVariable);
//        return this;
//    }
//
//    /**
//     * 设置参数
//     *
//     * @param target 对象
//     * @return this
//     */
//    public ExpressionParser setParserVariable(Object target) {
//        Class<?> aClass = target.getClass();
//        String name = aClass.getName();
//        context.setVariable("p" + (index.get()), target);
//        context.setVariable(name, target);
//        context.setVariable(NamingCase.toFirstLowerCase(name), target);
//        return this;
//    }
//
//    /**
//     * 设置参数
//     *
//     * @param method 方法
//     * @param args   参数
//     * @return this
//     */
//    public ExpressionParser setParserVariable(Method method, Object[] args) {
//        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
//            Object arg = args[i];
//            context.setVariable("p" + i, arg);
//            if (null == arg) {
//                continue;
//            }
//
//            Class<?> aClass = arg.getClass();
//            String name = aClass.getName();
//            context.setVariable(name, arg);
//            context.setVariable(NamingCase.toFirstLowerCase(name), arg);
//        }
//
//        context.setVariable("m", method);
//        context.setVariable("t", method.getDeclaringClass());
//        return this;
//    }
//
//    /**
//     * 编译
//     *
//     * @param express 表达式
//     * @return 结果
//     */
//    public Value<Object> compile(String express) {
//        return Value.of(parser.parseExpression(express).getValue());
//    }
//}
