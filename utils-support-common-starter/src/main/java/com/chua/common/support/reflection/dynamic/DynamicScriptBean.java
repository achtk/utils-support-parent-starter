package com.chua.common.support.reflection.dynamic;

import com.chua.common.support.lang.expression.ExpressionProvider;

/**
 * 动态对象
 *
 * @author ch
 */
@SuppressWarnings("unchecked")
public class DynamicScriptBean implements DynamicBean {


    private String source;
    private ExpressionProvider.ExpressionProviderBuilder builder;

    public DynamicScriptBean(String source, ExpressionProvider.ExpressionProviderBuilder builder) {
        this.source = source;
        this.builder = builder;
    }

    public static <T> DynamicBeanBuilder<T> newBuilder() {
        return new DynamicScriptBeanBeanBuilder<>();
    }


    public static class DynamicScriptBeanBeanBuilder<T> extends AbstractDynamicBeanStandardBuilder<T> {

        @Override
        public DynamicBean build() {
            return new DynamicScriptBean(source, ExpressionProvider.newBuilder(name));
        }
    }

    @Override
    public <T> T createBean(Class<T> type) {
        return builder.build().createProxy(type);
    }

    @Override
    public <T> Class<T> createType(Class<T> type) {
        builder.build().createProxy(type);
        return (Class<T>) builder.build().getType();
    }
}
