package com.chua.common.support.context.hook;

import com.chua.common.support.context.aware.DestructionAwareBeanPostProcessor;
import com.chua.common.support.context.factory.ConfigureApplicationContext;
import com.chua.common.support.context.process.BeanPostProcessor;
import com.chua.common.support.function.DisposableAware;

import java.security.AccessControlException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * hook
 *
 * @author CH
 */
public class ApplicationShutdownHook implements Runnable {

    private List<BeanPostProcessor> postProcessors;
    private ConfigureApplicationContext context;
    private final AtomicBoolean shutdownHookAdded = new AtomicBoolean();

    public ApplicationShutdownHook() {
    }

    @Override
    public void run() {
        addRuntimeShutdownHookIfNecessary();
        synchronized (ApplicationShutdownHook.class) {
            closeBeanAndWait();
        }
    }

    /**
     * 关闭Bean
     */
    private void closeBeanAndWait() {
        context.getBeanMap(DisposableAware.class);
    }


    /**
     * 添加hook
     */
    private void addRuntimeShutdownHookIfNecessary() {
        if (this.shutdownHookAdded.compareAndSet(false, true)) {
            addRuntimeShutdownHook();
        }
    }

    /**
     * 添加hook
     */
    void addRuntimeShutdownHook() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(this, "ApplicationShutdownHook"));
        } catch (AccessControlException ex) {
            // Not allowed in some environments
        }
    }


    /**
     * 注冊配置
     *
     * @param context 上下文
     */
    public void registerApplicationContext(ConfigureApplicationContext context) {
        this.context = context;
    }

}
