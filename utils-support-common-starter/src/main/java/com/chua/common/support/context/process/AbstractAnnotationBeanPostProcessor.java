package com.chua.common.support.context.process;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.context.definition.ClassDefinition;
import com.chua.common.support.context.definition.DefinitionUtils;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.context.constant.ContextConstant.COMPARATOR;

/**
 * 注解扫描器
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public abstract class AbstractAnnotationBeanPostProcessor<T extends Annotation> implements BeanPostProcessor {

    private static Reflections REFLECTIONS;
    private final ApplicationContextConfiguration contextConfiguration;
    protected final Map<String, SortedList<TypeDefinition>> table = new ConcurrentHashMap<>();

    public AbstractAnnotationBeanPostProcessor(ApplicationContextConfiguration contextConfiguration) {
        this.contextConfiguration = contextConfiguration;
        processInjection(null);
    }

    /**
     * 获取注解器
     *
     * @param contextConfiguration 配置
     * @return Reflections
     */
    public static Reflections getInstance(ApplicationContextConfiguration contextConfiguration) {
        if (null != REFLECTIONS) {
            return REFLECTIONS;
        }

        if(!contextConfiguration.getGlobal().openScanner()) {
            return REFLECTIONS;
        }

        initial(contextConfiguration);
        return REFLECTIONS;
    }

    /**
     * 初始化
     *
     * @param configuration 扫描位置
     */
    private static void initial(ApplicationContextConfiguration configuration) {
        if (null != REFLECTIONS) {
            return;
        }

        if(!configuration.getGlobal().openScanner()) {
            return;
        }


        if (null == configuration.getPackageScan() || configuration.getPackageScan().length == 0) {
            initialScan(null);
            return;
        }

        initialScan(configuration.getPackageScan());
    }

    /**
     * 初始化
     *
     * @param packages 包
     */
    private static void initialScan(String[] packages) {
        ConfigurationBuilder builder = ConfigurationBuilder.build();
        if (null != packages) {
            builder.forPackages(packages);
        } else {
            builder.setUrls(new ArrayList<>());
            builder.forPackages("");
        }


        builder.setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
                .setParallel(true)
                .addClassLoaders(ClassLoader.getSystemClassLoader());


        REFLECTIONS = new Reflections(builder);
    }

    @Override
    public void processInjection(TypeDefinition typeDefinition) {
        initial(contextConfiguration);

        if(null == REFLECTIONS) {
            return;
        }

        Set<Class<?>> annotatedWith = REFLECTIONS.getTypesAnnotatedWith(getAnnotation());
        for (Class<?> aClass : annotatedWith) {
            register(aClass);
        }
    }

    /**
     * 注册类
     *
     * @param type 类
     */
    protected void register(Class<?> type) {
        register(new ClassDefinition(type));
    }

    /**
     * 注册类
     *
     * @param definition 定义
     */
    protected void register(TypeDefinition definition) {
        DefinitionUtils.register(definition, table);
    }

    /**
     * 获取注解
     *
     * @return 注解
     */
    Class<T> getAnnotation() {
        return (Class<T>) ClassUtils.getActualTypeArguments(this.getClass())[0];
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
        rs.addAll(sortedList1);

        return rs;
    }

    @Override
    public <T> List<TypeDefinition<T>> postProcessInstantiation(Class<T> targetType) {
        SortedList sortedList = Optional.ofNullable(table.get(targetType.getTypeName())).orElse(SortedList.emptyList());
        return sortedList;
    }

    @Override
    public boolean isValid(TypeDefinition definition) {
        return false;
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

    /**
     * 卸载
     *
     * @param value      值
     * @param sortedList 待卸载的值
     */
    private void unProcessInjection(SortedList<TypeDefinition> value, SortedList<TypeDefinition> sortedList) {
        value.removeAll(sortedList);
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
}
