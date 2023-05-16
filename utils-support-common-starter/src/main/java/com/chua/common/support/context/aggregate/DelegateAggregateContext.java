package com.chua.common.support.context.aggregate;

import com.chua.common.support.collection.SortedArrayList;
import com.chua.common.support.collection.SortedList;
import com.chua.common.support.context.aggregate.scanner.DelegateScanner;
import com.chua.common.support.context.aggregate.scanner.Scanner;
import com.chua.common.support.context.enums.DefinitionType;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.context.factory.ConfigurableBeanFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聚合
 * @author CH
 */
public class DelegateAggregateContext implements AggregateContext{
    private ConfigurableBeanFactory configurableBeanFactory;
    private ApplicationContextConfiguration configuration;
    private Map<String, SortedList<Aggregate>> cache = new ConcurrentHashMap<>();
    private Scanner scanner;
    private static final Comparator<Aggregate> COMPARATOR = Comparator.comparingInt(Aggregate::order);


    public DelegateAggregateContext(ConfigurableBeanFactory configurableBeanFactory, ApplicationContextConfiguration configuration) {
        this.scanner = new DelegateScanner(configurableBeanFactory, configuration, configuration.isOnlyOriginal());
        this.configurableBeanFactory = configurableBeanFactory;
        this.configuration = configuration;
    }


    @Override
    public void mount(String name, Aggregate aggregate) {
        scanner.scan(aggregate);
        cache.computeIfAbsent(name, it -> new SortedArrayList<>(COMPARATOR)).add(aggregate);
    }

    @Override
    public synchronized void unmount(Aggregate aggregate) {
        configurableBeanFactory.removeBean(aggregate.getOriginal().toExternalForm(), DefinitionType.AGGREGATE);
        List<Aggregate> rpl = new LinkedList<>();
        for (SortedList<Aggregate> list : cache.values()) {
            for (Aggregate aggregate1 : list) {
                if(aggregate1 == aggregate) {
                    rpl.add(aggregate1);
                }
            }
        }

        for (SortedList<Aggregate> list : cache.values()) {
            list.removeAll(rpl);
        }
    }

    @Override
    public synchronized void unmount(String name) {
        SortedList<Aggregate> aggregates = cache.get(name);
        for (Aggregate aggregate : aggregates) {
            unmount(aggregate);
        }
        cache.remove(name);
    }

    @Override
    public Class<?> forName(String name) {
        SortedList<Aggregate> tpl = new SortedArrayList<>(COMPARATOR);
        for (SortedList<Aggregate> aggregates : cache.values()) {
            for (Aggregate aggregate : aggregates) {
                if(aggregate.contains(name)) {
                    tpl.add(aggregate);
                }
            }
        }

        Aggregate aggregate = tpl.first();
        return aggregate.forName(name);
    }
}
