package com.chua.common.support.spi.autowire;


import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.spi.finder.AbstractServiceFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * ServiceLoader
 *
 * @author CH
 * @see ServiceLoader
 */
public class ServiceLoaderServiceFinder extends AbstractServiceFinder {
    @Override
    protected List<ServiceDefinition> find() {
        List<ServiceDefinition> result = new ArrayList<>();
        ServiceLoader<?> serviceLoader = ServiceLoader.load(service, getClassLoader());
        for (Object t : serviceLoader) {
            result.addAll(buildDefinition(t));
        }
        return result;
    }
}
