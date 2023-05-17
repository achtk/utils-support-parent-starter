package com.chua.groovy.support.expression;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.expression.listener.Listener;
import com.chua.common.support.lang.expression.make.ExpressionMarker;
import com.chua.common.support.utils.ClassUtils;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * groovy
 *
 * @author CH
 */
@Spi("groovy")
public class GroovyExpressionMarker implements ExpressionMarker {

    public static CompilerConfiguration config = new CompilerConfiguration();
    private Class aClass;

    {
        config.setSourceEncoding("UTF-8");
    }

    @Override
    public Object createObject(Listener listener, ClassLoader classLoader, Object[] args) {
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader, config);
        try {
            this.aClass = groovyClassLoader.parseClass(listener.getSource());
            return ClassUtils.forObject(aClass, args);
        } catch (CompilationFailedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return aClass;
    }
}
