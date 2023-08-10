package com.chua.common.support.engine;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.lang.function.CosinSimilar;
import com.chua.common.support.lang.function.Similar;
import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.spi.autowire.AutoServiceAutowire;
import com.chua.common.support.spi.finder.SamePackageServiceFinder;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 引擎
 *
 * @author CH
 */
public abstract class EngineBase implements Engine {
    protected final DetectionConfiguration configuration;

    public EngineBase(DetectionConfiguration configuration) {
        this.configuration = configuration;
    }

    private final Map<Class<?>, Map<String, SortedSet<ServiceDefinition>>> definitions = new ConcurrentHashMap<>();

    private static final Comparator<ServiceDefinition> COMPARATOR = new Comparator<ServiceDefinition>() {
        @Override
        public int compare(ServiceDefinition o1, ServiceDefinition o2) {
            return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
        }
    };
    /**
     * 包
     * @return 包
     */
    protected abstract String getPackage();

    @Override
    @SuppressWarnings("ALL")
    public <T> T get(String name, Class<T> target) {
        Map<String, SortedSet<ServiceDefinition>> stringSortedSetMap = definitions.get(target);
        if(MapUtils.isEmpty(stringSortedSetMap)) {
            stringSortedSetMap = new ConcurrentHashMap<>();
            definitions.put(target, stringSortedSetMap);

            SamePackageServiceFinder serviceFinder = new SamePackageServiceFinder();
            serviceFinder.setService(target);
            List<Class<?>> subTypeByPackage = serviceFinder.findSubTypeByPackage(getPackage());
            List<ServiceDefinition> result = new ArrayList<>();
            for (Class<?> aClass : subTypeByPackage) {
                if (!target.isAssignableFrom(aClass) || aClass.isInterface() || Modifier.isAbstract(aClass.getModifiers())) {
                    continue;
                }
                List<ServiceDefinition> serviceDefinitions = serviceFinder.buildDefinition(aClass);
                if (!serviceDefinitions.isEmpty()) {
                    result.addAll(serviceDefinitions);
                    continue;
                }
                result.addAll(serviceFinder.buildDefinition(null, aClass, aClass.getTypeName(), null));
            }


            for (ServiceDefinition serviceDefinition : result) {
                stringSortedSetMap.computeIfAbsent(serviceDefinition.getName(), it -> new TreeSet<>(COMPARATOR))
                        .add(serviceDefinition);
            }
        }
        Object obj = newInstance(name, stringSortedSetMap, target, configuration.type());

        if(null != obj) {
            return (T) obj;
        }

        if (Similar.class.isAssignableFrom(target)) {
            return (T) new CosinSimilar();
        }

        return newFailureInstance(target, configuration.type());
    }

    public abstract  <T> T newFailureInstance(Class<T> target, String type);

    private Object newInstance(String name, Map<String, SortedSet<ServiceDefinition>> stringSortedSetMap, Class<?> target, String type) {
        if(StringUtils.isNotEmpty(name)) {
            return stringSortedSetMap.get(name).first().newInstance(new AutoServiceAutowire(), configuration);
        }

        if(StringUtils.isNotEmpty(type)) {
            try {
                return stringSortedSetMap.get(type.toUpperCase()).first().newInstance(new AutoServiceAutowire(), configuration);
            } catch (Exception e) {
                return null;
            }
        }
        return MapUtils.getFirst(stringSortedSetMap).getValue().first().newInstance(new AutoServiceAutowire(), configuration);
    }
}
