package com.chua.common.support.context.process;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.context.definition.*;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.utils.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.context.constant.ContextConstant.COMPARATOR;

/**
 * 类型注解器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class TypeBeanPostProcessor implements BeanPostProcessor {

    protected final Map<String, SortedList<TypeDefinition>> table = new ConcurrentHashMap<>();

    public TypeBeanPostProcessor(ApplicationContextConfiguration contextConfiguration) {
    }

    @Override
    public void processInjection(TypeDefinition definition) {
        DefinitionUtils.register(definition, table);
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(String bean, Class<T> targetType) {
        List rs = new LinkedList<>();
        if(null == bean) {
            bean = targetType.getTypeName();
        }
        SortedList<TypeDefinition> sortedList = table.get(bean);
        if (null == sortedList) {
            return rs;
        }

        for (TypeDefinition definition : sortedList) {
            if (definition.isAssignableFrom(targetType)) {
                rs.add(definition);
            }
        }
        SortedList<TypeDefinition> sortedList1 = table.get(targetType.getTypeName());
        if(null != sortedList1) {
            rs.addAll(sortedList1);
        }

        return rs;
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(Class<T> targetType) {
        SortedList sortedList = Optional.ofNullable(table.get(targetType.getTypeName())).orElse(SortedList.emptyList());
        return sortedList;
    }

    @Override
    public boolean isValid(TypeDefinition definition) {
        Class<? extends TypeDefinition> aClass = definition.getClass();
        return  aClass == ClassDefinition.class || aClass == ObjectDefinition.class || aClass == MethodDefinition.class;
    }

    @Override
    public void unProcessInjection(String name, DefinitionType definitionType) {
        if(definitionType == DefinitionType.AGGREGATE) {
            return;
        }

        SortedList<TypeDefinition> sortedList = table.get(name);
        if (CollectionUtils.isEmpty(sortedList)) {
            return;
        }

        table.remove(name);
        for (Map.Entry<String, SortedList<TypeDefinition>> entry : table.entrySet()) {
            unProcessInjection(entry.getValue(), sortedList);
            if (entry.getValue().isEmpty()) {
                table.remove(entry.getKey());
            }
        }

    }

    @Override
    public void refresh(ConfigurableBeanFactory standardConfigurableBeanFactory) {
        if (null == standardConfigurableBeanFactory) {
            return;
        }

        for (SortedList<TypeDefinition> value : table.values()) {
            DefinitionUtils.refresh(value, standardConfigurableBeanFactory);
        }
    }

    @Override
    public List<TypeDefinition<Object>> postBeanByMethod(Class<?>[] type) {
        List<TypeDefinition<Object>> rs = new SortedArrayList<>(COMPARATOR);
        Collection<SortedList<TypeDefinition>> values = table.values();
        for (SortedList<TypeDefinition> value : values) {
            for (TypeDefinition typeDefinition : value) {
                if(typeDefinition.hasMethodByParameterType(type)) {
                    rs.add(typeDefinition);
                }
            }
        }
        return rs;
    }

    /**
     * 卸载
     *
     * @param value      值
     * @param sortedList 待卸载的值
     */
    private void unProcessInjection(SortedList<TypeDefinition> value, SortedList<TypeDefinition> sortedList) {
        value.removeAll(sortedList);
    }
}
