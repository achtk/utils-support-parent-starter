package com.chua.common.support.context.outside;

import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigureApplicationContext;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.expression.ExpressionProvider;
import com.chua.common.support.monitor.Listener;
import com.chua.common.support.monitor.Monitor;
import com.chua.common.support.monitor.NotifyMessage;
import com.chua.common.support.monitor.NotifyType;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 外部上下文
 *
 * @author CH
 */
public class OutSideApplicationContext implements OutSideContext, InitializingAware, Listener<NotifyMessage>, DisposableAware {

    private ApplicationContextConfiguration configuration;
    private ConfigureApplicationContext context;

    private static final String LISTENER = "server.outside.listener";

    private Monitor monitor;

    private static final String OUTSIDE_BEAN_PREFIX = "outside.bean.";
    private String listener;

    @Override
    public OutSideContext configuration(ConfigureApplicationContext context) {
        this.context = context;
        this.configuration = context.getApplicationContextConfiguration();
        this.listener = this.configuration.getEnvironment().getProperty(LISTENER);
        if (StringUtils.isNullOrEmpty(listener)) {
            listener = "./outside-path";
        }
        try {
            FileUtils.forceMkdir(new File(listener));
        } catch (IOException ignored) {
        }
        return this;
    }

    @Override
    public void refresh() {
        doInitialRefresh();
    }

    /**
     * 初始化目录
     */
    private void doInitialRefresh() {
        File temp = new File(listener);
        if (!temp.exists()) {
            return;
        }
        File[] files = temp.listFiles();
        if (null == files) {
            return;
        }

        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            doInitialRefresh(file);
        }
    }

    /**
     * 初始化文件
     *
     * @param file 文件
     */
    private void doInitialRefresh(File file) {
        File[] files = file.listFiles();
        if (null == files) {
            return;
        }

        for (File file1 : files) {
            if (!file1.isFile()) {
                continue;
            }
            refresh(file1, NotifyType.CREATE);
        }
    }

    /**
     * 初始化对象
     *
     * @param temp 文件
     * @param kind 类型
     */
    private void refresh(File temp, NotifyType kind) {
        String baseName = FileUtils.getName(temp.getParent());
        if (!ClassUtils.isPresent(baseName)) {
            return;
        }
        Class<?> aClass = ClassUtils.forName(baseName);
        if (kind == NotifyType.CREATE) {
            ExpressionProvider provider = ExpressionProvider.newScript().script(temp.getAbsolutePath().replace(File.separator, "/")).build();
            context.registerBean(new ObjectDefinition(aClass)
                    .setObject(provider.createProxy(aClass))
                    .addBeanName(temp.getAbsolutePath(), FileUtils.getBaseName(temp))
                    .setProxy(true));
            return;
        }

        context.removeBean(temp.getAbsolutePath());
    }

    @Override
    public void destroy() {
        try {
            monitor.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        refresh();
        monitor = ServiceProvider.of(Monitor.class).getNewExtension("io");
        monitor.addListener(this);
        try {
            monitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEvent(NotifyMessage message) {
        File temp = new File(message.getMessage());
        if (message.getType() == NotifyType.CREATE) {
            if (!temp.exists() || !temp.isFile()) {
                return;
            }
            refresh(temp, message.getType());
        }

        if (message.getType() == NotifyType.DELETE) {
            refresh(temp, message.getType());
        }
    }
}
