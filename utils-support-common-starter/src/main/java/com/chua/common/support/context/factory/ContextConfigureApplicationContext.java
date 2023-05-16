package com.chua.common.support.context.factory;

import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.context.aggregate.AggregateContext;
import com.chua.common.support.context.aggregate.DelegateAggregateContext;
import com.chua.common.support.context.bean.BeanObject;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.environment.Environment;

import java.util.List;
import java.util.Map;

/**
 * 可配置的上下文
 *
 * @author CH
 */
public class ContextConfigureApplicationContext implements ConfigureApplicationContext {

    private final ApplicationContextConfiguration contextConfiguration;
    private final ConfigurableBeanFactory beanFactory;
    private final AggregateContext aggregateContext;

    public ContextConfigureApplicationContext(ApplicationContextConfiguration contextConfiguration) {
        this.contextConfiguration = contextConfiguration;
        this.aggregateContext = new DelegateAggregateContext(this, contextConfiguration);
        this.beanFactory = new DelegatyConfigurableBeanFactory(contextConfiguration, aggregateContext);
    }

    @Override
    public ApplicationContextConfiguration getApplicationContextConfiguration() {
        return contextConfiguration;
    }

    @Override
    public void refresh() {
        beanFactory.refresh();
    }


    @Override
    public <T> T getBean(String name, Class<T> target) {
        return beanFactory.getBean(name, target);
    }

    @Override
    public BeanObject getBean(String beanName) {
        return beanFactory.getBean(beanName);
    }


    @Override
    public void registerBean(TypeDefinition definition) {
        beanFactory.registerBean(definition);
    }

    @Override
    public <T> TypeDefinition<T> getDefinition(String name, Class<T> target) {
        return beanFactory.getDefinition(name, target);
    }

    @Override
    public <T> T getBean(Class<T> target) {
        return beanFactory.getBean(target);
    }

    @Override
    public <T> Map<String, T> getBeanMap(Class<T> target) {
        return beanFactory.getBeanMap(target);
    }

    @Override
    public void autowire(Object bean) {
        beanFactory.autowire(bean);
    }

    @Override
    public Map<String, TypeDefinition<Object>> getBeanByMethod(Class<?>... type) {
        return beanFactory.getBeanByMethod(type);
    }

    @Override
    public AggregateContext createAggregate() {
        return aggregateContext;
    }

    @Override
    public void removeBean(String name, DefinitionType definitionType) {
        beanFactory.removeBean(name, definitionType);
    }

    @Override
    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public <T> List<T> getAnyBean(Class<T> targetType) {
        return beanFactory.getAnyBean(targetType);
    }

    @Override
    public Environment getEnvironment() {
        return contextConfiguration.getEnvironment();
    }

    @Override
    public void mount(String name, Aggregate aggregate) {
        aggregateContext.mount(name, aggregate);
    }

    @Override
    public void unmount(Aggregate aggregate) {
        aggregateContext.unmount(aggregate);
    }

    @Override
    public void unmount(String name) {
        aggregateContext.unmount(name);
    }

    @Override
    public Class<?> forName(String name) {
        return aggregateContext.forName(name);
    }
}
