package com.chua.common.support.lang.expression.make;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.lang.compile.Compiler;
import com.chua.common.support.lang.compile.JavassistCompiler;
import com.chua.common.support.lang.compile.JdkCompiler;
import com.chua.common.support.lang.expression.listener.Listener;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.Md5Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * script
 *
 * @author CH
 */
@Spi("java")
public class JavaScriptExpression implements ExpressionMarker {

    private static final Compiler JAVASSIST_COMPILER = new JavassistCompiler();
    private static final Compiler JDK_COMPILER = new JdkCompiler();
    private static final Map<String, AtomicInteger> CNT = new ConcurrentHashMap<>();
    private Class<?> compiler;


    @Override
    public Object createObject(Listener listener, ClassLoader classLoader, Object[] args) {
        String expression = listener.getSource();
        if (null == expression) {
            return null;
        }

        String key = Md5Utils.getInstance().getMd5String(expression);
        String suffix = "";
        if (CNT.containsKey(key)) {
            suffix = CNT.get(key).incrementAndGet() + "";
        } else {
            CNT.put(key, new AtomicInteger());
        }
        try {
            this.compiler = JAVASSIST_COMPILER.compiler(expression, classLoader, suffix);
            return ClassUtils.forObject(compiler, args);
        } catch (Exception ignored) {
        }
        try {
            compiler = JDK_COMPILER.compiler(expression, classLoader);
            return ClassUtils.forObject(compiler, args);
        } catch (Throwable ignored) {
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return compiler;
    }
}
