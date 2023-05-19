package com.chua.common.support.context.factory;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.context.environment.StandardEnvironment;
import com.chua.common.support.context.hook.ApplicationShutdownHook;
import com.chua.common.support.context.process.BeanPostProcessor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 上下文工厂
 *
 * @author CH
 */
@Accessors(fluent = true)
@Data
@NoArgsConstructor(staticName = "newBuilder")
public class ApplicationContextBuilder {

    private static final Map<String, ConfigureApplicationContext> CACHE = new ConcurrentHashMap<>();

    private String name = "default";
    /**
     * 配置处理器
     */
    private Environment environment = new StandardEnvironment();
    /**
     * 对象工厂
     */
    private BeanFactory beanFactory;
    /**
     * 是否开启扫描位置
     */
    private boolean openScanner = true;
    /**
     * 扫描位置
     */
    private String[] scan;
    /**
     * 是否扫描 aggregate下的bean注入到上下文中
     */
    private boolean openAggregateScanner = true;

    /**
     * 是否只扫描原始包
     */
    private boolean onlyOriginal = true;

    private GlobalConfiguration global = new GlobalConfiguration();
    /**
     * 加载器
     */
    private List<BeanPostProcessor> processors = new LinkedList<>();


    public ApplicationContextBuilder addProcess(BeanPostProcessor processor) {
        this.processors.add(processor);
        return this;
    }

    /**
     * 初始化
     *
     * @return 上下文
     */
    public ConfigureApplicationContext build() {
        return CACHE.computeIfAbsent(name, s -> {
            prepareEnvironment();
            ApplicationContextConfiguration contextConfiguration = createApplicationContextConfiguration();
            ConfigureApplicationContext context =
                    new ContextConfigureApplicationContext(contextConfiguration);
            prepareContext(context);
            refreshContext(context);
            afterRefresh(context);
            return context;
        });
    }

    private void prepareEnvironment() {
        if(null == environment) {
            environment = new StandardEnvironment();
        }
    }

    /**
     * 初始化
     *
     * @return ApplicationContextConfiguration
     */
    private ApplicationContextConfiguration createApplicationContextConfiguration() {
        ApplicationContextConfiguration contextConfiguration = new ApplicationContextConfiguration();
        BeanUtils.copyProperties(this, contextConfiguration);
        contextConfiguration.setPackageScan(scan);
        contextConfiguration.setGlobal(global);
        global.openScanner(openScanner);
        contextConfiguration.setProcessors(processors);
        contextConfiguration.setEnvironment(environment.contextConfiguration(contextConfiguration));
        return contextConfiguration;
    }

    /**
     * 结束处理
     *
     * @param context 上下文
     */
    private void afterRefresh(ConfigureApplicationContext context) {
        new ApplicationShutdownHook()
                .registerApplicationContext(context);
    }

    /**
     * 刷新上下文
     *
     * @param context 上下文
     */
    private void refreshContext(ConfigureApplicationContext context) {
        context.refresh();
    }

    /**
     * 预处理
     *
     * @param context 上下文
     */
    private void prepareContext(ConfigureApplicationContext context) {
        context.registerBean(new ObjectDefinition<>(context));
    }
}
