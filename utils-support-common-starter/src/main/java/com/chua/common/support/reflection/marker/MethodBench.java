package com.chua.common.support.reflection.marker;

import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.reflection.describe.CraftDescribe;
import com.chua.common.support.reflection.describe.MethodDescribe;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.NullValue;
import com.chua.common.support.value.Value;

/**
 * 方法执行器
 *
 * @author CH
 */
public class MethodBench implements Bench {

    private final MethodDescribe methodDescribe;
    private transient final Object entity;
    private final String express;

    public MethodBench(MethodDescribe methodDescribe, Object entity, String express) {
        this.methodDescribe = methodDescribe;
        this.entity = entity;
        this.express = express;
    }

    @Override
    public Class<?> returnType() {
        return ClassUtils.forName(methodDescribe.returnType());
    }

    @Override
    public Value<Object> execute(Object... args) {
        if (methodDescribe instanceof MethodBenchFactory.VoidMethodDescribe && !StringUtils.isNullOrEmpty(express)) {
            return executeExpress(args);
        }
        return methodDescribe.invoke(entity, args);
    }

    /**
     * 执行表达式
     *
     * @param args 参数
     * @return 结果
     */
    private Value<Object> executeExpress(Object[] args) {
        return NullValue.INSTANCE;
    }

    @Override
    public Value<Object> executeBean(Object entity, Object[] args, Object... plugins) {
        return methodDescribe.invoke(entity, args, plugins);
    }


    /**
     * 执行
     *
     * @param craftDescribe 参数
     * @return 结果
     */
    @Override
    public Value<Object> execute(CraftDescribe craftDescribe) {
        return execute(craftDescribe.obj() == null ? entity : craftDescribe.obj(), craftDescribe.parameters().toArray());
    }
}
