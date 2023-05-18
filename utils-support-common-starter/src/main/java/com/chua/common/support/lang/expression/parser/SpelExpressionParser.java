//package com.chua.common.support.lang.expression.parser;
//
//import com.chua.common.support.value.Value;
//import org.springframework.expression.EvaluationContext;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//
///**
// * 解析器
// * @author CH
// */
//public class SpelExpressionParser implements ExpressionParser{
//
//    final org.springframework.expression.spel.standard.SpelExpressionParser springExpre = new org.springframework.expression.spel.standard.SpelExpressionParser();
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
//        return Value.of(springExpre.parseExpression(express).getValue(context));
//    }
//}
