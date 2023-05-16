package com.chua.common.support.context.factory;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.context.aggregate.AggregateContext;
import com.chua.common.support.context.bean.BeanObject;
import com.chua.common.support.context.definition.ObjectDefinition;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.context.process.BeanPostProcessorFactory;
import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.utils.ClassUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author CH
 */
@SuppressWarnings("ALL")
public class DelegatyConfigurableBeanFactory implements ConfigurableBeanFactory {

    private final ApplicationContextConfiguration contextConfiguration;

    private final BeanPostProcessorFactory beanPostProcessorFactory;
    private final Map<Class, Object> cache = new ConcurrentReferenceHashMap<>(512);
    private final AggregateContext aggregateContext;


    public DelegatyConfigurableBeanFactory(ApplicationContextConfiguration contextConfiguration, AggregateContext aggregateContext) {
        this.beanPostProcessorFactory = new BeanPostProcessorFactory(contextConfiguration);
        this.contextConfiguration = contextConfiguration;
        this.aggregateContext = aggregateContext;
    }


    @Override
    public void registerBean(TypeDefinition definition) {
        beanPostProcessorFactory.register(definition);
    }

    @Override
    public <T> TypeDefinition<T> getDefinition(String name, Class<T> target) {
        return beanPostProcessorFactory.getBean(name, target);
    }

    @Override
    public void removeBean(String name, DefinitionType definitionType) {
        beanPostProcessorFactory.remove(name, definitionType);
    }

    @Override
    public <T> List<T> getAnyBean(Class<T> targetType) {
        List<TypeDefinition<T>> anyBean = beanPostProcessorFactory.getAnyBean(targetType);
        List<T> rs = new LinkedList<>();
        for (TypeDefinition<T> tTypeDefinition : anyBean) {
            T object = tTypeDefinition.getObject(this);
            if (null == object) {
                continue;
            }

            rs.add(object);
        }

        return rs;
    }

    @Override
    public Environment getEnvironment() {
        return contextConfiguration.getEnvironment();
    }

    @Override
    public void refresh() {
        beanPostProcessorFactory.refresh(this);
    }

    @Override
    public synchronized <T> T getBean(String name, Class<T> target) {
        TypeDefinition<T> bean = beanPostProcessorFactory.getBean(name, target);
        if (null == bean ) {
            if(!ClassUtils.isJavaType(target)) {
                return null;
            }
            return (T) cache.computeIfAbsent(target, it -> ProxyUtils.newProxy(target, new BridgingMethodIntercept<T>(target, null)));
        }

        try {
            return bean.getObject(this);
        } catch (Throwable e) {
            return (T) cache.computeIfAbsent(target, it -> ProxyUtils.newProxy(target, new BridgingMethodIntercept<T>(target, null)));
        }
    }

    @Override
    public BeanObject getBean(String beanName) {
        TypeDefinition<Object> bean = beanPostProcessorFactory.getBean(beanName);
        if(null == bean) {
            return BeanObject.EMPTY;
        }
        return new BeanObject(bean, bean.getObject(this), this);
    }

    @Override
    public <T> T getBean(Class<T> target) {
        return getBean(null, target);
    }

    @Override
    public <T> Map<String, T> getBeanMap(Class<T> target) {
        Map<String, TypeDefinition<T>> beanMap = beanPostProcessorFactory.getBeanMap(target);
        Map<String, T> rs = new HashMap<>(beanMap.size());
        for (Map.Entry<String, TypeDefinition<T>> entry : beanMap.entrySet()) {
            TypeDefinition<T> tTypeDefinition = entry.getValue();
            T object = tTypeDefinition.getObject(this);
            if (null == object) {
                continue;
            }

            rs.put(entry.getKey(), object);

        }
        return rs;
    }

    @Override
    public void autowire(Object bean) {
        TypeDefinition<Object> objectTypeDefinition = ObjectDefinition.of(bean);
        objectTypeDefinition.setProxy(false);
        objectTypeDefinition.getObject(this, true);
    }

    @Override
    public Map<String, TypeDefinition<Object>> getBeanByMethod(Class<?>... type) {
        return beanPostProcessorFactory.getBeanByMethod(type);
    }

    @Override
    public AggregateContext createAggregate() {
        return aggregateContext;
    }
}
