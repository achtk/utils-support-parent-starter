package com.chua.common.support.context.definition;

import com.chua.common.support.context.aggregate.Aggregate;
import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.spi.ServiceProvider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 对象
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class AggregateSpiDefinition<T> extends AggregateDefinition<T> {

    private final Aggregate aggregate;

    public AggregateSpiDefinition(Aggregate aggregate) {
        super(aggregate, (Class<T>) Object.class, new String[0]);
        this.aggregate = aggregate;
    }

    /**
     * 原始文件
     * @return 原始文件
     */
    public String getOriginal() {
        return aggregate.getOriginal().toExternalForm();
    }

    public Set<TypeDefinition> postProcessInstantiation(String name, Class targetType) {
        ServiceProvider<T> serviceProvider = ServiceProvider.newBuilder().classloader(aggregate.getClassLoader())
                .build(targetType);

        Set<TypeDefinition> rs = new LinkedHashSet<>();
        if(null == name) {
            Collection<Class<T>> values = serviceProvider.listType().values();
            for (Class<? extends T> value : values) {
                rs.add(new ClassDefinition(value));
            }
        } else {
            ServiceDefinition definition = serviceProvider.getDefinition(name);
            if(null != definition) {
                Class<?> implClass = definition.getImplClass();
                if(null != implClass) {
                    rs.add(new ClassDefinition(implClass));
                }
            }
        }
        return rs;
    }
}
