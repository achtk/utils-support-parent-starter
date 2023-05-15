package com.chua.common.support.spi;


import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.NameAware;
import com.chua.common.support.spi.autowire.AutoServiceAutowire;
import com.chua.common.support.spi.autowire.ServiceAutowire;
import com.chua.common.support.spi.autowire.ServiceLoaderServiceFinder;
import com.chua.common.support.spi.finder.CustomServiceFinder;
import com.chua.common.support.spi.finder.SamePackageServiceFinder;
import com.chua.common.support.spi.finder.ServiceFinder;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.Value;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;

/**
 * spi
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ServiceProvider<T> implements InitializingAware {

    private static final ServiceProvider EMPTY = new ServiceProvider(null, null, null);
    private static final Map<Class<?>, ServiceProvider> CACHE = new ConcurrentHashMap<>();
    private final Value<Class<T>> value;
    private final ClassLoader classLoader;
    private final ServiceAutowire serviceAutowire;

    private final Map<String, SortedSet<ServiceDefinition>> definitions = new ConcurrentHashMap<>();

    private static final Comparator<ServiceDefinition> COMPARATOR = new Comparator<ServiceDefinition>() {
        @Override
        public int compare(ServiceDefinition o1, ServiceDefinition o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    };
    private T defaultImpl;
    private static final ServiceDefinition defaultDefinition = new ServiceDefinition();

    /**
     * 初始化
     *
     * @param value 类型
     */
    public static <T> ServiceProvider<T> of(Class<T> value) {
        return of(value, ClassUtils.getDefaultClassLoader(), null);
    }

    /**
     * 初始化
     *
     * @param value       类型
     * @param classLoader 类加载器
     */
    public static <T> ServiceProvider<T> of(Class<T> value, ClassLoader classLoader) {
        return of(value, classLoader);
    }

    /**
     * 初始化
     *
     * @param value           类型
     * @param classLoader     类加载器
     * @param serviceAutowire 注入器
     */
    public static <T> ServiceProvider<T> of(Class<T> value, ClassLoader classLoader, ServiceAutowire serviceAutowire) {
        if (ClassUtils.isVoid(value)) {
            return EMPTY;
        }
        return CACHE.computeIfAbsent(value, it -> new ServiceProvider<>(value, classLoader, serviceAutowire));
    }

    /**
     * 初始化
     *
     * @param value       类型
     * @param classLoader 类加载器
     */
    private ServiceProvider(Class<T> value, ClassLoader classLoader, ServiceAutowire serviceAutowire) {
        this.value = Value.of(value);
        this.classLoader = Optional.ofNullable(classLoader).orElse(ClassLoader.getSystemClassLoader());
        this.serviceAutowire = Optional.ofNullable(serviceAutowire).orElse(AutoServiceAutowire.INSTANCE);
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        if (value.isNull()) {
            return;
        }

        List<ServiceFinder> finders = getFinders();
        this.findDefinition(finders);
    }

    /**
     * 保存定义
     *
     * @param finders 查找器
     */
    private void findDefinition(List<ServiceFinder> finders) {
        for (ServiceFinder finder : finders) {
            List<ServiceDefinition> analyze = finder.analyze(value.getValue(), classLoader);
            registerDefinition(analyze);
        }
    }

    /**
     * 保存定义
     *
     * @param finders 查找器
     */
    private void registerDefinition(List<ServiceDefinition> analyze) {
        for (ServiceDefinition serviceDefinition : analyze) {
            definitions.computeIfAbsent(serviceDefinition.getName(), it -> new TreeSet<>(COMPARATOR)).add(serviceDefinition);
            if (serviceDefinition.isDefault()) {
                registerDefault(serviceDefinition);
            }
        }
    }

    /**
     * 默认实现
     *
     * @param serviceDefinition 定义
     */
    private void registerDefault(ServiceDefinition serviceDefinition) {
        this.defaultImpl = serviceDefinition.getObj(serviceAutowire);
    }

    /**
     * 查找器
     *
     * @return 查找器
     */
    private List<ServiceFinder> getFinders() {
        List<ServiceFinder> rs = new LinkedList<>();
        rs.add(new ServiceLoaderServiceFinder());
        rs.add(new CustomServiceFinder());
        rs.add(new SamePackageServiceFinder());

        return rs;
    }

    //Definition *******************************************************************************************************************
    public ServiceDefinition getDefinition(String name, Object... args) {
        SortedSet<ServiceDefinition> definitions = new TreeSet<>(COMPARATOR);
        for (String item : name.split(SYMBOL_COMMA)) {
            SortedSet<ServiceDefinition> definitions1 = getDefinitions(item, args);
            if (null == definitions1) {
                continue;
            }
            definitions.addAll(definitions1);
            definitions.addAll(createNameAware(name, definitions1, args));
        }


        return definitions.isEmpty() ? defaultDefinition : definitions.first();
    }


    public SortedSet<ServiceDefinition> getDefinitions(String name, Object... args) {
        SortedSet<ServiceDefinition> rs = new TreeSet<>(COMPARATOR);
        name = name.toLowerCase();

        SortedSet<ServiceDefinition> serviceDefinitions = definitions.get(name.toLowerCase());
        if (null != serviceDefinitions) {
            rs.addAll(serviceDefinitions);
        }

        for (Map.Entry<String, SortedSet<ServiceDefinition>> entry : definitions.entrySet()) {
            if (name.equals(entry.getKey())) {
                continue;
            }

            SortedSet<ServiceDefinition> entryValue = entry.getValue();
            rs.addAll(createNameAware(name, entryValue, args));
        }

        return rs;
    }

    /**
     * 获取命名
     *
     * @param name        名称
     * @param definitions 定义
     * @param args        参数
     * @return 命名
     */
    private <T> Collection<? extends ServiceDefinition> createNameAware(String name, SortedSet<ServiceDefinition> definitions, Object[] args) {
        List<ServiceDefinition> rs = new ArrayList<>(definitions.size());
        for (ServiceDefinition definition : definitions) {
            if (!definition.isPresent()) {
                continue;
            }

            T obj = definition.newInstance(serviceAutowire, args);
            if (null == obj) {
                continue;
            }

            if (!containsName(((NameAware) obj).named(), name)) {
                continue;
            }

            rs.add(definition);
        }
        return rs;
    }

    /**
     * 是否包含名称
     *
     * @param named 实现类名称
     * @param name  名称
     * @return 结果
     */
    private boolean containsName(String[] named, String name) {
        for (String s : named) {
            if (s.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    //collect *******************************************************************************************************************

    /**
     * 获取实现
     *
     * @param args 參數
     * @return 实现
     */
    public Map<String, Class<? extends T>> listType() {
        if (definitions.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Class<? extends T>> result = new HashMap<>(definitions.size());

        for (SortedSet<ServiceDefinition> value : this.definitions.values()) {
            ServiceDefinition noneObject = value.first();
            if (null == noneObject) {
                continue;
            }
            result.put(noneObject.getName(), (Class<? extends T>) noneObject.implClass);
        }
        return result;
    }
    /**
     * 获取实现
     *
     * @param args 參數
     * @return 实现
     */
    public Map<String, T> list(Object... args) {
        if (definitions.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, T> result = new HashMap<>(definitions.size());

        for (SortedSet<ServiceDefinition> value : this.definitions.values()) {
            ServiceDefinition noneObject = value.first();
            if (null == noneObject) {
                continue;
            }
            result.put(noneObject.getName(), noneObject.newInstance(serviceAutowire, args));
        }
        return result;
    }


    //get *******************************************************************************************************************

    /**
     * 获取实现(每次初始化)
     *
     * @param name 名称
     * @param args 參數
     * @return 实现
     */
    public T getNewExtension(String name, Object... args) {
        if (null == name) {
            return definitions.size() == 1 ? definitions.values().iterator().next().first().newInstance(serviceAutowire, args) : defaultImpl;
        }

        return (T) Optional.ofNullable(getDefinition(name).newInstance(serviceAutowire, args)).orElse(defaultImpl);
    }

    /**
     * 获取实现(每次初始化)
     *
     * @param name 名称
     * @return 实现
     */
    public T getNewExtension(String name) {
        if (null == name) {
            return definitions.size() == 1 ? definitions.values().iterator().next().first().getObj(serviceAutowire) : defaultImpl;
        }

        return (T) Optional.ofNullable(getDefinition(name).newInstance(serviceAutowire)).orElse(defaultImpl);
    }

    /**
     * 获取实现
     *
     * @param name 名称
     * @return 实现
     */
    public T getExtension(String name) {
        if (null == name) {
            return definitions.size() == 1 ? definitions.values().iterator().next().first().getObj(serviceAutowire) : defaultImpl;
        }

        return (T) Optional.ofNullable(getDefinition(name).getObj(serviceAutowire)).orElse(defaultImpl);
    }
}
