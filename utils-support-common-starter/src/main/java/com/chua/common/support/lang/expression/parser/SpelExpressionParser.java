//package com.chua.common.support.lang.expression.parser;
//
//import com.chua.common.support.json.jsonpath.internal.EvaluationContext;
//import com.chua.common.support.value.Value;
//
///**
// * 解析器
// * @author CH
// */
//public class SpelExpressionParser implements ExpressionParser{
//
//    final org.springframework.expression.spel.standard.SpelExpressionParser spelExpressionParser = new org.springframework.expression.spel.standard.SpelExpressionParser();
//    final EvaluationContext context = new StandardEvaluationContext();
//
//    @Override
//    public ExpressionParser setVariable(String name, Object value) {
//        context.setVariable(name, value);
//        return this;
//    }
//
//    @Override
//    public Value<?> parseExpression(String express) {
//        return Value.of(spelExpressionParser.parseExpression(express).getValue(context));
//    }
//}
