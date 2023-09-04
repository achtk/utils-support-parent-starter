package com.chua.common.support.objects;

import com.chua.common.support.collection.SortedList;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.environment.StandardConfigureEnvironment;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.objects.source.TypeDefinitionSourceFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础配置
 * @author CH
 * @date 2023/09/01
 */
@Slf4j
public class StandardConfigureObjectContext implements ConfigureObjectContext, InitializingAware {

    private final ConfigureContextConfiguration configuration;
    private StandardConfigureEnvironment configureEnvironment;
    private TypeDefinitionSourceFactory typeDefinitionSourceFactory;

    public StandardConfigureObjectContext(ConfigureContextConfiguration configuration) {
        this.configuration = configuration;
        this.afterPropertiesSet();
    }

    @Override
    public StandardConfigureEnvironment getEnvironment() {
        return configureEnvironment;
    }

    @Override
    public void afterPropertiesSet() {
        if (log.isDebugEnabled()) {
            log.debug("初始化环境");
        }
        this.configureEnvironment = new StandardConfigureEnvironment(configuration.environmentConfiguration(), configuration.propertySources());
        if (log.isDebugEnabled()) {
            log.debug("初始化定义");
        }
        this.typeDefinitionSourceFactory = new TypeDefinitionSourceFactory(configuration);
    }

    @Override
    public <T> T getBean(String name, Class<T> targetType) {
        return typeDefinitionSourceFactory.getBean(name, targetType);
    }

    @Override
    public Object getBean(String name) {
        return typeDefinitionSourceFactory.getBean(name);
    }

    @Override
    public SortedList<TypeDefinition> getBeanDefinition(String name) {
        return typeDefinitionSourceFactory.getBeanDefinition(name);
    }

    @Override
    public <T> ObjectProvider<T> getBean(Class<T> targetType) {
        return typeDefinitionSourceFactory.getBean(targetType);
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {
        typeDefinitionSourceFactory.unregister(typeDefinition);
    }

    @Override
    public void unregister(String name) {
        typeDefinitionSourceFactory.unregister(name);
    }

    @Override
    public void register(TypeDefinition definition) {
        typeDefinitionSourceFactory.register(definition);
    }
}
