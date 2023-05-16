package com.chua.common.support.context.process;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.context.definition.AggregateDefinition;
import com.chua.common.support.context.definition.AggregateSpiDefinition;
import com.chua.common.support.context.definition.DefinitionUtils;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.chua.common.support.context.constant.ContextConstant.COMPARATOR;

/**
 * 类型注解器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class AggregateBeanPostProcessor implements BeanPostProcessor {

    private final Map<String, Map<String, SortedList<TypeDefinition>>> table = new ConcurrentHashMap<>();
    private final Map<String, SortedList<AggregateSpiDefinition>> spiTable = new ConcurrentHashMap<>();

    public AggregateBeanPostProcessor(ApplicationContextConfiguration contextConfiguration) {
    }

    @Override
    public void processInjection(TypeDefinition definition) {
        AggregateDefinition aggregateDefinition = (AggregateDefinition) definition;
        Map<String, SortedList<TypeDefinition>> tpl = new LinkedHashMap<>();
        String original = aggregateDefinition.getOriginal();
        if(aggregateDefinition instanceof AggregateSpiDefinition) {
            spiTable.computeIfAbsent(original, it -> new SortedArrayList<>(COMPARATOR)).add((AggregateSpiDefinition) aggregateDefinition);
            return;
        }

        DefinitionUtils.register(aggregateDefinition, tpl);
        for (Map.Entry<String, SortedList<TypeDefinition>> entry : tpl.entrySet()) {
            table.computeIfAbsent(original, new Function<String, Map<String, SortedList<TypeDefinition>>>() {
                @Override
                public Map<String, SortedList<TypeDefinition>> apply(String s) {
                    return new ConcurrentHashMap<>();
                }
            }).computeIfAbsent(entry.getKey(), it -> new SortedArrayList<>(COMPARATOR)).addAll(entry.getValue());
        }
    }



    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(String bean, Class<T> targetType) {
        SortedList rs = new SortedArrayList<>(COMPARATOR);
        for (Map<String, SortedList<TypeDefinition>> map : table.values()) {
            SortedList<TypeDefinition> sortedList = map.get(targetType.getTypeName());
            if(null == sortedList) {
                continue;
            }

            for (TypeDefinition definition : sortedList) {
                if (definition.isAssignableFrom(targetType)) {
                    rs.add(definition);
                }
            }

            SortedList sortedList1 = map.get(targetType.getTypeName());
            if(null != sortedList1) {
                rs.addAll(sortedList1);
            }

        }

        for (SortedList<AggregateSpiDefinition> list : spiTable.values()) {
            doAnalysisAggregateSpi(list, rs, bean, targetType);
        }

        return rs;
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(Class<T> targetType) {
        SortedList tpl = new SortedArrayList<>(COMPARATOR);
        for (Map<String, SortedList<TypeDefinition>> map : table.values()) {
            tpl.addAll(Optional.ofNullable(map.get(targetType.getTypeName())).orElse(SortedList.emptyList()));
        }

        for (SortedList<AggregateSpiDefinition> list : spiTable.values()) {
            doAnalysisAggregateSpi(list, tpl, null, targetType);
        }
        return tpl;
    }

    private <T> void doAnalysisAggregateSpi(SortedList<AggregateSpiDefinition> list, SortedList tpl, String bean, Class<T> targetType) {
        for (AggregateSpiDefinition aggregateSpiDefinition : list) {
            Set set = aggregateSpiDefinition.postProcessInstantiation(bean, targetType);
            if(null == set) {
                continue;
            }
            tpl.addAll(set);
        }
    }

    @Override
    public boolean isValid(TypeDefinition definition) {
        Class<? extends TypeDefinition> aClass = definition.getClass();
        return AggregateDefinition.class.isAssignableFrom(aClass);
    }

    @Override
    public void unProcessInjection(String name, DefinitionType definitionType) {
        if(definitionType == DefinitionType.NONE) {
            return;
        }

        table.remove(name);
        spiTable.remove(name);
    }

    @Override
    public void refresh(ConfigurableBeanFactory standardConfigurableBeanFactory) {
        if (null == standardConfigurableBeanFactory) {
            return;
        }

        for (Map<String, SortedList<TypeDefinition>> stringSortedListMap : table.values()) {
            for (SortedList<TypeDefinition> value : stringSortedListMap.values()) {
                DefinitionUtils.refresh(value, standardConfigurableBeanFactory);
            }
        }
    }

    @Override
    public List<TypeDefinition<Object>> postBeanByMethod(Class<?>[] type) {
        List<TypeDefinition<Object>> rs = new SortedArrayList<>(COMPARATOR);
        for (Map<String, SortedList<TypeDefinition>> stringSortedListMap : table.values()) {
            Collection<SortedList<TypeDefinition>> values = stringSortedListMap.values();
            for (SortedList<TypeDefinition> value : values) {
                for (TypeDefinition typeDefinition : value) {
                    if(typeDefinition.hasMethodByParameterType(type)) {
                        rs.add(typeDefinition);
                    }
                }
            }
        }

        return rs;
    }

}
