package com.chua.common.support.objects;


import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.objects.environment.StandardConfigureEnvironment;

/**
 * 对象管理器
 *
 * @author CH
 */
public interface ConfigureObjectContext extends ObjectContext {
    /**
     * 获取环境
     *
     * @return 环境
     */
    StandardConfigureEnvironment getEnvironment();


    /**
     * 获取表达式解析器
     *
     * @return {@link ExpressionParser}
     */
    ExpressionParser getExpressionParser();
    /**
     * 自动装配
     *
     * @param bean bean
     */
    void autowire(Object bean);


    /**
     * 新默认值
     *
     * @return {@link ConfigureObjectContext}
     */
    static  ConfigureObjectContext newDefault() {
        return new StandardConfigureObjectContext(ConfigureContextConfiguration.builder().build());
    }
}
