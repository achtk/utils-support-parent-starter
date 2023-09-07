package com.chua.common.support.objects.source;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.lang.expression.parser.ExpressionParser;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.ConfigureObjectContext;
import com.chua.common.support.objects.bean.BeanObject;
import com.chua.common.support.objects.bean.SingleBeanObject;
import com.chua.common.support.objects.definition.TypeDefinition;
import com.chua.common.support.objects.environment.StandardConfigureEnvironment;
import com.chua.common.support.objects.provider.ObjectProvider;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.objects.source.AbstractTypeDefinitionSource.COMPARABLE;

/**
 * 来源工厂
 *
 * @author CH
 * @since 2023/09/02
 */
public class TypeDefinitionSourceFactory implements ConfigureObjectContext {

    private final ConfigureContextConfiguration configuration;
    final List<TypeDefinitionSource> definitionSources;
    private final StandardConfigureEnvironment configureEnvironment;
    private final ExpressionParser expressionParser;

    public TypeDefinitionSourceFactory(ConfigureContextConfiguration configuration, StandardConfigureEnvironment configureEnvironment, ExpressionParser expressionParser) {
        this.configuration = configuration;
        this.definitionSources = ServiceProvider.of(TypeDefinitionSource.class).collect(configuration);
        this.configureEnvironment = configureEnvironment;
        this.expressionParser = expressionParser;
    }

    @Override
    public <T> T getBean(String name, Class<T> targetType) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARABLE);
        for (TypeDefinitionSource definitionSource : definitionSources) {
            sortedList.addAll(definitionSource.getBean(name, targetType));
        }

        return ObjectUtils.withNull(sortedList.first(), it -> it.newInstance(this));
    }

    @Override
    public BeanObject getBean(String name) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARABLE);
        for (TypeDefinitionSource definitionSource : definitionSources) {
            sortedList.addAll(definitionSource.getBean(name));
        }

        return new SingleBeanObject(sortedList.first(), this);
    }

    @Override
    public SortedList<TypeDefinition> getBeanDefinition(String name) {
        SortedList<TypeDefinition> sortedList = new SortedArrayList<>(COMPARABLE);
        for (TypeDefinitionSource definitionSource : definitionSources) {
            sortedList.addAll(definitionSource.getBean(name));
        }

        return sortedList.first().newInstance(this);
    }

    @Override
    public <T> ObjectProvider<T> getBean(Class<T> targetType) {
        return new ObjectProvider<>(targetType, getBeanOfType(targetType), this);
    }

    @Override
    public <T> Map<String, T> getBeanOfType(Class<T> targetType) {
        Map<String, T> rs = new LinkedHashMap<>();
        Map<String, SortedList<TypeDefinition>> tmp = new LinkedHashMap<>();
        for (TypeDefinitionSource definitionSource : definitionSources) {
            SortedList<TypeDefinition> bean = definitionSource.getBean(targetType);
            doAnalysis(tmp, bean);
        }

        for (Map.Entry<String, SortedList<TypeDefinition>> entry : tmp.entrySet()) {
            rs.put(entry.getKey(), entry.getValue().first().newInstance(this));
        }

        return rs;
    }

    /**
     * 做分析
     *
     * @param tmp  tmp
     * @param bean bean
     */
    private <T> void doAnalysis(Map<String, SortedList<TypeDefinition>> tmp, SortedList<TypeDefinition> bean) {
        for (TypeDefinition typeDefinition : bean) {
            String[] name = typeDefinition.getName();
            for (String s : name) {
                tmp.computeIfAbsent(s, it -> new SortedArrayList<>(COMPARABLE)).add(typeDefinition);
            }
        }
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {
        for (TypeDefinitionSource definitionSource : definitionSources) {
            if (definitionSource.isMatch(typeDefinition)) {
                definitionSource.unregister(typeDefinition);
                break;
            }
        }
    }

    @Override
    public void unregister(String name) {
        for (TypeDefinitionSource definitionSource : definitionSources) {
            definitionSource.unregister(name);
        }
    }

    @Override
    public TypeDefinition register(TypeDefinition definition) {
        for (TypeDefinitionSource definitionSource : definitionSources) {
            if (definitionSource.isMatch(definition)) {
                definitionSource.register(definition);
                break;
            }
        }
        return definition;
    }

    @Override
    public Map<String, TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType) {
        Map<String, TypeDefinition> rs = new LinkedHashMap<>();
        Map<String, SortedList<TypeDefinition>> tmp = new LinkedHashMap<>();
        for (TypeDefinitionSource definitionSource : definitionSources) {
            SortedList<TypeDefinition> bean = definitionSource.getBeanByMethod(annotationType);
            doAnalysis(tmp, bean);
        }

        for (Map.Entry<String, SortedList<TypeDefinition>> entry : tmp.entrySet()) {
            rs.put(entry.getKey(), entry.getValue().first().newInstance(this));
        }

        return rs;
    }

    @Override
    public StandardConfigureEnvironment getEnvironment() {
        return configureEnvironment;
    }

    @Override
    public ExpressionParser getExpressionParser() {
        return expressionParser;
    }

    @Override
    public void autowire(Object bean) {

    }
}
