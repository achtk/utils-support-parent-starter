package com.chua.common.support.context.definition;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.expression.ExpressionProvider;
import com.chua.common.support.lang.expression.listener.Listener;
import com.chua.common.support.utils.ClassUtils;

/**
 * 源码定义
 *
 * @author CH
 */
public class SourceDefinition<T> extends ClassDefinition<T> implements TypeDefinition<T>, InitializingAware {

    private final Class<T> type;
    private String sourceType;

    private final String source;

    private Listener listener;

    public SourceDefinition(Class<T> type, ClassLoader classLoader, String source, String sourceType, String... name) {
        this(type, classLoader, source, sourceType, null, name);
    }

    public SourceDefinition(Class<T> type, String source, String sourceType, String... name) {
        this(type, ClassUtils.getDefaultClassLoader(), source, sourceType, null, name);
    }

    public SourceDefinition(Class<T> type, ClassLoader classLoader, String source, String sourceType, Listener listener, String... name) {
        super(type, name);
        this.type = type;
        this.sourceType = sourceType;
        this.single(true);
        this.source = source;
        this.listener = listener;
        this.object = ExpressionProvider.newBuilder(sourceType)
                .source(source)
                .scriptType(sourceType)
                .classLoader(classLoader)
                .listener(listener).build().createProxy(type);
    }


    @Override
    public synchronized T getObject(Object... args) {
        return object;
    }


}
