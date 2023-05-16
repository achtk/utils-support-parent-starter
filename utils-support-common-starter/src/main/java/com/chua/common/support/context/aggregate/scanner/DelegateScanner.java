package com.chua.common.support.context.aggregate.scanner;

import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.context.aggregate.adaptor.AggregateAdaptor;
import com.chua.common.support.context.aggregate.adaptor.AnnotationAggregateAdaptor;
import com.chua.common.support.context.aggregate.adaptor.SpiAggregateAdaptor;
import com.chua.common.support.context.definition.TypeDefinition;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.scanners.Scanners;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;
import com.chua.common.support.spi.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * 代理扫描器
 *
 * @author CH
 */
@Slf4j
public class DelegateScanner implements Scanner {
    private ConfigurableBeanFactory configurableBeanFactory;
    private ApplicationContextConfiguration configuration;
    private boolean original;

    /**
     * 初始化
     *
     * @param configurableBeanFactory 上下文
     * @param configuration           配置
     * @param original                是否只扫描原始包
     */
    public DelegateScanner(ConfigurableBeanFactory configurableBeanFactory, ApplicationContextConfiguration configuration, boolean original) {
        this.configurableBeanFactory = configurableBeanFactory;
        this.configuration = configuration;
        this.original = original;
    }

    @Override
    public void scan(Aggregate aggregate) {
        if (!configuration.isOpenAggregateScanner()) {
            return;
        }

        Reflections reflections = createReflections(aggregate);
        Collection<AggregateAdaptor> aggregateAdaptors = ServiceProvider.of(AggregateAdaptor.class).list().values();
        for (AggregateAdaptor aggregateAdaptor : aggregateAdaptors) {
            if(aggregateAdaptor instanceof AnnotationAggregateAdaptor) {
                Set<Class<? extends Annotation>> annotation = ((AnnotationAggregateAdaptor) aggregateAdaptor).getScanAnnotation();
                doAnalysis(aggregate, (AnnotationAggregateAdaptor) aggregateAdaptor, annotation, reflections);
                continue;
            }

            if(aggregateAdaptor instanceof SpiAggregateAdaptor) {
                Set<TypeDefinition<?>> definition = ((SpiAggregateAdaptor) aggregateAdaptor).createDefinition(aggregate);
                for (TypeDefinition<?> typeDefinition : definition) {
                    configurableBeanFactory.registerBean(typeDefinition);
                }
            }
        }

    }

    /**
     * 解析
     *
     * @param aggregate        聚合
     * @param aggregateAdaptor 聚合
     * @param annotation       注解
     * @param reflections      扫描器
     */
    private void doAnalysis(Aggregate aggregate, AnnotationAggregateAdaptor aggregateAdaptor, Set<Class<? extends Annotation>> annotation, Reflections reflections) {
        for (Class<? extends Annotation> aClass : annotation) {
            Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(aClass);
            for (Class<?> aClass1 : typesAnnotatedWith) {
                TypeDefinition<?> definition = aggregateAdaptor.createDefinition(aggregate, aClass, aClass1);
                if(null == definition) {
                    continue;
                }
                configurableBeanFactory.registerBean(definition);
            }
        }
    }


    /**
     * 初始化
     *
     * @param aggregate 聚合
     */
    private Reflections createReflections(Aggregate aggregate) {
        ConfigurationBuilder builder = ConfigurationBuilder.build();
        if (original) {
            builder.setUrls(aggregate.getOriginal());
        } else {
            builder.setUrls(new ArrayList<>());
        }
        builder.forPackages("");


        builder.setScanners(Scanners.TypesAnnotated)
                .setParallel(true)
                .setClassLoaders(new ClassLoader[]{aggregate.getClassLoader()});


        return new Reflections(builder);
    }

}
