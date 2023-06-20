package com.chua.common.support.lang.lambda;


import com.chua.common.support.utils.ClassUtils;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * 基于 {@link SerializedLambda} 创建的元信息
 * <p>
 * Create by hcl at 2021/7/7
 */
public class ShadowLambdaMeta implements LambdaMeta {
    private final SerializedLambda lambda;

    public ShadowLambdaMeta(SerializedLambda lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(SYMBOL_SEMICOLON))
                .replace(SYMBOL_LEFT_SLASH, SYMBOL_DOT);
        return ClassUtils.toClassConfident(instantiatedType, lambda.getCapturingClass().getClassLoader());
    }

}
