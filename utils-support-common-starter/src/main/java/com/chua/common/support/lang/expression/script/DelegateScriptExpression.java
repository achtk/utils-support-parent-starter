package com.chua.common.support.lang.expression.script;

import com.chua.common.support.lang.expression.listener.FileListener;
import com.chua.common.support.lang.expression.listener.Listener;
import com.chua.common.support.lang.expression.make.ExpressionMarker;
import com.chua.common.support.lang.proxy.DelegateMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyMethod;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * script
 *
 * @author CH
 */
public class DelegateScriptExpression implements ScriptExpression {

    private final File file;
    private final ClassLoader classLoader;
    private final Listener listener;
    private final ExpressionMarker expressionMarker;
    private Object[] args;

    protected Object bean;

    private AtomicBoolean status = new AtomicBoolean(false);
    private final Lock lock = new ReentrantLock();

    public DelegateScriptExpression(File file, ClassLoader classLoader, Listener listener, Object... args) {
        this.file = file;
        this.classLoader = classLoader;
        this.listener = listener;
        this.args = args;
        this.expressionMarker = ServiceProvider.of(ExpressionMarker.class).getNewExtension(FileUtils.getExtension(file));
        this.bean = expressionMarker.createObject(listener, classLoader, args);
        this.status.set(true);
    }

    public DelegateScriptExpression(File file, Listener listener, Object... args) {
        this(file, ClassLoader.getSystemClassLoader(), listener, args);
    }

    public DelegateScriptExpression(File file, Object... args) {
        this(file, new FileListener(file), args);
    }

    @Override
    public <T> T create(Class<T> type) {
        return (T) bean;
    }

    @Override
    public Class<?> getType() {
        return expressionMarker.getType();
    }

    @Override
    @SuppressWarnings("ALL")
    public <T> T createProxy(Class<T> type) {
        return (T) ProxyUtils.newProxy(type, new DelegateMethodIntercept<T>(type, (Function<ProxyMethod, Object>) proxyMethod -> {
            check();
            if (null == bean) {
                return null;
            }

            try {
                return proxyMethod.invoke(bean, args);
            } catch (Exception e) {
                return null;
            }
        }));
    }

    /**
     * 检测脚本
     */
    protected void check() {
        if (listener.isChange()) {
            lock.lock();
            try {
                bean = expressionMarker.createObject(listener, classLoader, args);
            } finally {
                lock.unlock();
            }
        }
    }


}
