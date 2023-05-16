package com.chua.common.support.lang.expression.source;

import com.chua.common.support.lang.expression.listener.Listener;
import com.chua.common.support.lang.expression.make.ExpressionMarker;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.spi.ServiceProvider;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * source
 *
 * @author CH
 */
public class DelegateSourceExpression implements SourceExpression {

    private final String source;
    private final ClassLoader classLoader;
    private final Listener listener;
    private final ExpressionMarker expressionMarker;
    private Object[] args;

    protected Object bean;

    private final AtomicBoolean status = new AtomicBoolean(false);
    private final Lock lock = new ReentrantLock();

    public DelegateSourceExpression(String source, String type, ClassLoader classLoader, Listener listener, Object... args) {
        this.source = source;
        this.classLoader = classLoader;
        this.listener = listener;
        this.args = args;
        this.expressionMarker = ServiceProvider.of(ExpressionMarker.class).getNewExtension(type);
        this.bean = expressionMarker.createObject(listener, classLoader, args);
        this.status.set(true);
    }

    public DelegateSourceExpression(String source, String type, Listener listener, Object... args) {
        this(source, type, ClassLoader.getSystemClassLoader(), listener, args);
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
    public <T> T createProxy(Class<T> type) {
        return ProxyUtils.newProxy(type, (obj, method, args, proxy, plugins) -> {
            check();
            if (null == bean) {
                return null;
            }

            method.setAccessible(true);
            try {
                return method.invoke(bean, args);
            } catch (Exception e) {
                return null;
            }
        });
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
