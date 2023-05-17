package com.chua.common.support.extra.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * spring工具类
 * @author CH
 */
public class SpringUtils implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    /**
     * 上下文
     */
    private static final ThreadLocal<ApplicationContext> APPLICATION_CONTEXT = new ThreadLocal<ApplicationContext>() {
        private final Lock LOCK = new ReentrantLock();
        private ApplicationContext applicationContext;

        @Override
        public ApplicationContext get() {
            try {
                return applicationContext;
            } finally {
                super.get();
            }
        }

        @Override
        public void set(ApplicationContext value) {
            LOCK.lock();
            try {
                super.set(value);
                this.applicationContext = value;
            } finally {
                LOCK.unlock();
            }
        }
    };

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        SpringUtils.setApplicationContext(applicationContext);
    }



    /**
     * 设置上下文
     *
     * @param applicationContext 上下文
     * @see ApplicationContext
     * @see org.springframework.context.ApplicationContextAware
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT.set(applicationContext);
    }

    /**
     * 获取上下文
     *
     * @return 上下文
     * @see ApplicationContext
     * @see org.springframework.context.ApplicationContextAware
     */
    public static ApplicationContext getApplicationContext() {
        ApplicationContext applicationContext = null;
        try {
            return (applicationContext = APPLICATION_CONTEXT.get());
        } finally {
            APPLICATION_CONTEXT.set(applicationContext);
            APPLICATION_CONTEXT.remove();
        }
    }

}
