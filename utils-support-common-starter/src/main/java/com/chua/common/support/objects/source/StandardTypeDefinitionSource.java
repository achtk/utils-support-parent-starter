package com.chua.common.support.objects.source;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.ConfigureContextConfiguration;
import com.chua.common.support.objects.definition.ClassTypeDefinition;
import com.chua.common.support.objects.definition.MethodTypeDefinition;
import com.chua.common.support.objects.definition.TypeDefinition;

import java.lang.annotation.Annotation;

/**
 * 类型定义源
 *
 * @author CH
 * @since 2023/09/02
 */
@SuppressWarnings("ALL")
@Spi("standard")
public class StandardTypeDefinitionSource extends AbstractTypeDefinitionSource implements InitializingAware {


    public StandardTypeDefinitionSource(ConfigureContextConfiguration configuration) {
        super(configuration);
    }

    @Override
    public boolean isMatch(TypeDefinition typeDefinition) {
        return typeDefinition instanceof ClassTypeDefinition;
    }

    @Override
    public void unregister(TypeDefinition typeDefinition) {
        for (SortedList<TypeDefinition> list : nameDefinitions.values()) {
            list.remove(typeDefinition);
        }

        for (SortedList<TypeDefinition> definitionSortedList : typeDefinitions.values()) {
            definitionSortedList.remove(typeDefinition);
        }
    }

    @Override
    public void unregister(String name) {
        SortedList<TypeDefinition> sortedList = nameDefinitions.get(name);
        SortedList<TypeDefinition> sortedList1 = typeDefinitions.get(name);
        if (null != sortedList1) {
            sortedList1.removeAll(sortedList);
        }
    }

    @Override
    public void afterPropertiesSet() {

    }


    @Override
    public SortedList<TypeDefinition> getBeanByMethod(Class<? extends Annotation> annotationType) {
        SortedList<TypeDefinition> rs = new SortedArrayList<>(COMPARABLE);
        for (SortedList<TypeDefinition> value : nameDefinitions.values()) {
            doBeanByMethod(rs, value, annotationType);
        }
        return rs;
    }

    /**
     * 做bean通过方法
     *
     * @param rs             rs
     * @param value          值
     * @param annotationType 注解类型
     */
    private void doBeanByMethod(SortedList<TypeDefinition> rs, SortedList<TypeDefinition> value, Class<? extends Annotation> annotationType) {
        for (TypeDefinition typeDefinition : value) {
            if (typeDefinition instanceof MethodTypeDefinition) {
                if (typeDefinition.hasAnnotation(annotationType)) {
                    rs.add(typeDefinition);
                }
            }
        }
    }
}
