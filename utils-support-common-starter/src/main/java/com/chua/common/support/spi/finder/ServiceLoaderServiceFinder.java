package com.chua.common.support.spi.finder;


import com.chua.common.support.spi.ServiceDefinition;

import java.util.ArrayList;
import java.util.Iterator;
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
        Iterator<?> iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            Object next = null;
            try {
                next = iterator.next();
            } catch (Throwable e) {
                continue;
            }
            result.addAll(buildDefinition(next));
        }
        return result;
    }
}
