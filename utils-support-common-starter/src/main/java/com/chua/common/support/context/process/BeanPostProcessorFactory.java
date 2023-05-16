package com.chua.common.support.context.process;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;

import java.util.*;

import static com.chua.common.support.context.constant.ContextConstant.COMPARATOR;

/**
 * 加载器工厂
 *
 * @author CH
 */
public class BeanPostProcessorFactory {

    private final List<BeanPostProcessor> processor;
    private ApplicationContextConfiguration contextConfiguration;


    public BeanPostProcessorFactory(ApplicationContextConfiguration contextConfiguration) {
        this.contextConfiguration = contextConfiguration;
        List<BeanPostProcessor> beanPostProcessors = new ArrayList<>(ServiceProvider.of(BeanPostProcessor.class).list(contextConfiguration).values());
        if (null != contextConfiguration) {
            beanPostProcessors.addAll(contextConfiguration.getProcessors());
        }
        this.processor = Collections.unmodifiableList(beanPostProcessors);
    }

    /**
     * 注册
     *
     * @param definition 定义
     */
    public void register(TypeDefinition definition) {
        for (BeanPostProcessor processor : processor) {
            if (processor.isValid(definition)) {
                processor.processInjection(definition);
            }
        }
    }

    /**
     * 获取对象
     *
     * @param beanName   名称
     * @param targetType 类型
     * @param <T>        类型
     * @return 结果
     */
    public <T> TypeDefinition<T> getBean(String beanName, Class<T> targetType) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARATOR);
        for (BeanPostProcessor beanPostProcessor : processor) {
            List<TypeDefinition<T>> typeDefinitions = beanPostProcessor.postProcessInstantiation(beanName, targetType);
            if (CollectionUtils.isEmpty(typeDefinitions)) {
                continue;
            }

            sortedList.addAll(typeDefinitions);
        }
        return sortedList.isEmpty() ? null : sortedList.first();
    }

    /**
     * 获取对象
     *
     * @param targetType 类型
     * @param <T>        类型
     * @return 结果
     */
    public <T> TypeDefinition<T> getBean(Class<T> targetType) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARATOR);
        for (BeanPostProcessor beanPostProcessor : processor) {
            List<TypeDefinition<T>> typeDefinitions = beanPostProcessor.postProcessInstantiation(targetType);
            if (CollectionUtils.isEmpty(typeDefinitions)) {
                continue;
            }

            sortedList.addAll(typeDefinitions);
        }
        return sortedList.isEmpty() ? null : sortedList.first();
    }

    /**
     * 根据名字获取定义
     *
     * @param beanName 名称
     * @param <T>      类型
     * @return 定义
     */

    public <T> TypeDefinition<T> getBean(String beanName) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARATOR);
        for (BeanPostProcessor beanPostProcessor : processor) {
            List<TypeDefinition<Object>> typeDefinitions = beanPostProcessor.postProcessInstantiation(beanName, Object.class);
            if (CollectionUtils.isEmpty(typeDefinitions)) {
                continue;
            }

            sortedList.addAll(typeDefinitions);
        }
        return sortedList.isEmpty() ? null : sortedList.first();
    }

    /**
     * 根据类型获取定义
     *
     * @param targetType 类型
     * @param <T>        类型
     * @return 定义
     */
    public <T> Map<String, TypeDefinition<T>> getBeanMap(Class<T> targetType) {
        Map<String, SortedList<TypeDefinition<T>>> sortedList = new LinkedHashMap<>();
        for (BeanPostProcessor beanPostProcessor : processor) {
            List<TypeDefinition<T>> typeDefinitions = beanPostProcessor.postProcessInstantiation(targetType);
            if (CollectionUtils.isEmpty(typeDefinitions)) {
                continue;
            }

            register(sortedList, typeDefinitions);
        }

        Map<String, TypeDefinition<T>> rs = new LinkedHashMap<>();
        for (Map.Entry<String, SortedList<TypeDefinition<T>>> entry : sortedList.entrySet()) {
            rs.put(entry.getKey(), entry.getValue().first());
        }

        return rs;
    }

    /**
     * 删除定义
     *
     * @param name           名称
     * @param definitionType 类型
     * @return 定义
     */
    public void remove(String name, DefinitionType definitionType) {
        for (BeanPostProcessor beanPostProcessor : processor) {
            beanPostProcessor.unProcessInjection(name, definitionType);
        }
    }

    /**
     * 根据类型获取定义
     *
     * @param targetType 类型
     * @param <T>        类型
     * @return 定义
     */
    public <T> List<TypeDefinition<T>> getAnyBean(Class<T> targetType) {
        List<TypeDefinition<T>> rs = new LinkedList<>();
        for (BeanPostProcessor beanPostProcessor : processor) {
            List<TypeDefinition<T>> typeDefinitions = beanPostProcessor.postProcessInstantiation(targetType);
            if (CollectionUtils.isEmpty(typeDefinitions)) {
                continue;
            }

            rs.addAll(typeDefinitions);
        }

        return rs;
    }

    /**
     * 刷新定义
     *
     * @param standardConfigurableBeanFactory 工厂
     */
    public void refresh(ConfigurableBeanFactory standardConfigurableBeanFactory) {
        for (BeanPostProcessor beanPostProcessor : processor) {
            beanPostProcessor.refresh(standardConfigurableBeanFactory);
        }
    }

    /**
     * 获取方法类型对应的bean
     *
     * @param type 类型
     * @return 定义
     */
    public Map<String, TypeDefinition<Object>> getBeanByMethod(Class<?>... type) {
        Map<String, SortedList<TypeDefinition<Object>>> sortedList = new LinkedHashMap<>();
        for (BeanPostProcessor beanPostProcessor : processor) {
            List<TypeDefinition<Object>> typeDefinitions = beanPostProcessor.postBeanByMethod(type);
            if (CollectionUtils.isEmpty(typeDefinitions)) {
                continue;
            }

            register(sortedList, typeDefinitions);
        }

        Map<String, TypeDefinition<Object>> rs = new LinkedHashMap<>();
        for (Map.Entry<String, SortedList<TypeDefinition<Object>>> entry : sortedList.entrySet()) {
            rs.put(entry.getKey(), entry.getValue().first());
        }

        return rs;
    }

    /**
     * 解析结果
     *
     * @param sortedList      数据
     * @param typeDefinitions 定义
     * @param <T>             类型
     */
    private <T> void register(Map<String, SortedList<TypeDefinition<T>>> sortedList,
                              List<TypeDefinition<T>> typeDefinitions) {
        for (TypeDefinition<T> typeDefinition : typeDefinitions) {
            register(sortedList, typeDefinition);
        }
    }

    /**
     * 解析结果
     *
     * @param sortedList     数据
     * @param typeDefinition 定义
     * @param <T>            类型
     */
    private <T> void register(Map<String, SortedList<TypeDefinition<T>>> sortedList,
                              TypeDefinition<T> typeDefinition) {
        String[] beanName = typeDefinition.getBeanName();

        if (beanName.length != 0) {
            for (String s : beanName) {
                sortedList.computeIfAbsent(s, it -> new SortedArrayList<>(COMPARATOR)).add(typeDefinition);
            }
            return;
        }

        sortedList.computeIfAbsent(typeDefinition.getType().getTypeName(), it -> new SortedArrayList<>(COMPARATOR)).add(typeDefinition);
    }
}
