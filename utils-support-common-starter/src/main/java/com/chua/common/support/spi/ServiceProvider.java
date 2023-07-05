package com.chua.common.support.spi;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.NameAware;
import com.chua.common.support.spi.autowire.AutoServiceAutowire;
import com.chua.common.support.spi.autowire.ServiceAutowire;
import com.chua.common.support.spi.finder.*;
import com.chua.common.support.utils.*;
import com.chua.common.support.value.Value;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.spi.autowire.AutoServiceAutowire.UTILS;

/**
 * spi
 *
 * @author CH
 */
@SuppressWarnings("ALL")
public class ServiceProvider<T> implements InitializingAware {

    private static final ServiceProvider EMPTY = new ServiceProvider(null, null, null);
    private static Map<Class<?>, ServiceProvider> CACHE = new ConcurrentHashMap<>();
    private final List<ServiceFinder> finders = new LinkedList<>();

    private final Value<Class<T>> value;
    private ClassLoader classLoader;
    private ServiceAutowire serviceAutowire;
    private static final Map<Class<?>, String> SPI_NAME = new HashMap<>();
    protected static final CustomServiceFinder DEFAULT_FINDER = new CustomServiceFinder();

    private final Map<String, SortedSet<ServiceDefinition>> definitions = new ConcurrentHashMap<>();
    private static final Map<ClassLoader, Map<Class<?>, ServiceProvider>> SERVICE_PROVIDER_MAP = new ConcurrentHashMap<>();

    private static final Comparator<ServiceDefinition> COMPARATOR = new Comparator<ServiceDefinition>() {
        @Override
        public int compare(ServiceDefinition o1, ServiceDefinition o2) {
            return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
        }
    };
    private T defaultImpl;
    private static final ServiceDefinition DEFAULT_DEFINITION = new ServiceDefinition();

    static {
        List<ServiceEnvironment> serviceEnvironments = ServiceProvider.of(ServiceEnvironment.class).collect();
        for (ServiceEnvironment serviceEnvironment : serviceEnvironments) {
            serviceEnvironment.afterPropertiesSet();
        }
    }

    /**
     * 初始化
     *
     * @param value 类型
     */
    public static <T> ServiceProvider<T> of(String value) {
        if (!ClassUtils.isPresent(value)) {
            return EMPTY;
        }
        Class<?> aClass = ClassUtils.forName(value);
        return (ServiceProvider<T>) of(aClass);
    }

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
        if(null == CACHE) {
            CACHE = new ConcurrentHashMap<>();
        }
        return MapUtils.computeIfAbsent(CACHE, value, it -> {
            ServiceProvider<T> provider = new ServiceProvider<>(value, classLoader, serviceAutowire);
            provider.afterPropertiesSet();
            return provider;
        });
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
        if(ClassUtils.isPresent(UTILS)) {
            rs.add(new SpringServiceFinder());
        }
        rs.add(new ScriptServiceFinder());

        return rs;
    }

    //register *******************************************************************************************************************
    public void register(Object ref) {
        List<ServiceDefinition> serviceDefinitions = DEFAULT_FINDER.buildDefinition(ref);
        for (ServiceDefinition serviceDefinition : serviceDefinitions) {
            String name = serviceDefinition.getName();
            definitions.computeIfAbsent(name, it -> new TreeSet<>(COMPARATOR)).add(serviceDefinition);
        }
    }

    public void register(String name, Object ref) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setObj(ref);
        serviceDefinition.setType(value.getValue());
        serviceDefinition.setImplClass(ref.getClass());
        definitions.computeIfAbsent(name, it -> new TreeSet<>(COMPARATOR)).add(serviceDefinition);
    }

    public void register(String name, Class<T> ref) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setImplClass(ref);
        serviceDefinition.setType(value.getValue());
        definitions.computeIfAbsent(name, it -> new TreeSet<>(COMPARATOR)).add(serviceDefinition);

    }

    //Definition *******************************************************************************************************************
    public ServiceDefinition getDefinition(String name, Object... args) {
        name = name.toUpperCase();
        SortedSet<ServiceDefinition> definitions = new TreeSet<>(COMPARATOR);
        for (String item : name.split(SYMBOL_COMMA)) {
            SortedSet<ServiceDefinition> definitions1 = getDefinitions(item, args);
            if (null == definitions1) {
                continue;
            }
            definitions.addAll(definitions1);
            definitions.addAll(createNameAware(name, definitions1, args));
        }


        return definitions.isEmpty() ? DEFAULT_DEFINITION : definitions.first();
    }


    public SortedSet<ServiceDefinition> getDefinitions(String name, Object... args) {
        SortedSet<ServiceDefinition> rs = new TreeSet<>(COMPARATOR);

        SortedSet<ServiceDefinition> serviceDefinitions = definitions.get(name);
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
     * 映射
     *
     * @return 映射
     */
    public Map<String, Class<T>> mapping() {
        return listType();
    }

    /**
     * 获取实现
     *
     * @param args 參數
     * @return 实现
     */
    public Map<String, Class<T>> listType() {
        if (definitions.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Class<T>> result = new HashMap<>(definitions.size());

        for (SortedSet<ServiceDefinition> value : this.definitions.values()) {
            if (value.isEmpty()) {
                continue;
            }
            ServiceDefinition noneObject = value.first();
            if (null == noneObject || null == noneObject.getName()) {
                continue;
            }
            result.put(noneObject.getName(), (Class<T>) noneObject.implClass);
        }
        return result;
    }

    /**
     * 获取实现
     *
     * @param args 參數
     * @return 实现
     */
    public List<T> collect() {
        return Collections.unmodifiableList(new ArrayList<>(list().values()));
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

    /**
     * 获取实现
     *
     * @param consumer 消费者
     * @param args     參數
     * @return 实现
     */
    public void forEach(BiConsumer<String, T> consumer, Object... args) {
        list(args).forEach(consumer);
    }
    /**
     * 获取实现
     *
     * @param consumer 消费者
     * @param args     參數
     * @return 实现
     */
    public void forDefinitionEach(Consumer<ServiceDefinition> consumer) {
        for (Map.Entry<String, SortedSet<ServiceDefinition>> entry : definitions.entrySet()) {
            SortedSet<ServiceDefinition> value = entry.getValue();
            consumer.accept(value.first());
        }
    }
    /**
     * 遍历
     */
    public void moreEach(BiConsumer<String, T> consumer) {
        for (Map.Entry<String, SortedSet<ServiceDefinition>> entry : definitions.entrySet()) {
            SortedSet<ServiceDefinition> value = entry.getValue();
            lo: for (ServiceDefinition definition : value) {
                T imageConverter = definition.getObj(serviceAutowire);
                if (null == imageConverter) {
                    continue;
                }
                consumer.accept(entry.getKey(), imageConverter);
                break lo;
            }
        }
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
     * @param args 参数
     * @return 实现
     */
    public T getNewExtension(Enum name, Object... args) {
        return getNewExtension(name.name(), args);
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
     * 获取实现(每次初始化)
     *
     * @param name 名称
     * @return 实现
     */
    public T getDeepNewExtension(String name, Object... args) {
        if (null == name) {
            return defaultImpl;
        }

        ServiceDefinition definition = getDefinition(name);
        if (DEFAULT_DEFINITION != definition) {
            return (T) Optional.ofNullable(definition.newInstance(serviceAutowire, args)).orElse(defaultImpl);
        }

        while (StringUtils.isNotEmpty(name)) {
            name = FileUtils.getSimpleExtension(name);
            definition = getDefinition(name);
            if(DEFAULT_DEFINITION != definition) {
                return (T) Optional.ofNullable(definition.newInstance(serviceAutowire, args)).orElse(defaultImpl);
            }
        }

        return defaultImpl;
    }

    /**
     * 获取实现(每次初始化)
     *
     * @param name 名称
     * @return 实现
     */
    public Optional<T> getIfPresent(String name) {
        if (null == name) {
            return definitions.size() == 1 ? Optional.ofNullable(definitions.values().iterator().next().first().getObj(serviceAutowire)) : Optional.empty();
        }

        return Optional.ofNullable(getDefinition(name).newInstance(serviceAutowire));
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

    /**
     * 获取实现
     *
     * @param name 名称
     * @return 实现
     */
    public T getExtension(Enum name) {
        return null == name ? defaultImpl : getExtension(name.name().toLowerCase());
    }

    public T getSpiService() {
        Preconditions.checkArgument(!value.isNull());
        String s = SPI_NAME.get(value.getValue());
        if (StringUtils.isEmpty(s)) {
            Spi spi = value.getValue().getDeclaredAnnotation(Spi.class);
            if (null == spi) {
                throw new IllegalStateException("The " + value.getValue().getName() + " must contain the [@Spi] annotation!");
            }

            String[] value = spi.value();
            if (value.length == 0) {
                return null;
            }
            s = value[0];
            SPI_NAME.putIfAbsent(this.value.getValue(), s);
        }
        return getExtension(s);
    }


    /**
     * 初始化spi
     *
     * @param <T> 类型
     * @return this
     */
    public static <T> ServiceProviderBuilder<T> newBuilder() {
        return new ServiceProviderBuilder<T>();
    }

    /**
     * 是否包含实现
     *
     * @param name 名称
     * @return 是否包含实现
     */
    public boolean isSupport(String name) {
        return definitions.containsKey(name.toUpperCase());
    }

    public List<Option<String>> options() {
        if (definitions.isEmpty()) {
            return Collections.emptyList();
        }

        List<Option<String>> rs = new LinkedList<>();

        for (SortedSet<ServiceDefinition> value : this.definitions.values()) {
            if (value.isEmpty()) {
                continue;
            }
            ServiceDefinition noneObject = value.first();
            if (null == noneObject || null == noneObject.getName()) {
                continue;
            }
            rs.add(new Option<String>(noneObject.getName(), noneObject.getLabel(), noneObject.getLabelType()));
        }
        return rs;
    }

    /**
     * 构建类
     */
    public static class ServiceProviderBuilder<T> {

        private boolean openLog;

        private ClassLoader classLoader;
        private ServiceAutowire serviceAutowire = new AutoServiceAutowire();
        private final List<ServiceFinder> finders = new LinkedList<>();

        /**
         * 开启日志
         *
         * @return this
         */
        public ServiceProviderBuilder<T> openLog() {
            this.openLog = true;
            return this;
        }

        /**
         * 装配器
         *
         * @return this
         */
        public ServiceProviderBuilder<T> serviceAutowire(ServiceAutowire serviceAutowire) {
            this.serviceAutowire = serviceAutowire;
            return this;
        }

        /**
         * 类加载器
         *
         * @return this
         */
        public ServiceProviderBuilder<T> classloader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        /**
         * 设置发现器
         *
         * @return this
         */
        public ServiceProviderBuilder<T> finder(ServiceFinder... finders) {
            this.finders.addAll(Arrays.asList(finders));
            return this;
        }

        /**
         * 构建
         *
         * @param target 目标类型
         * @return 实体
         */
        public ServiceProvider<T> build(Class<T> target) {
            ClassLoader classLoader1 = Optional.ofNullable(classLoader).orElse(ClassUtils.getDefaultClassLoader());
            return SERVICE_PROVIDER_MAP
                    .computeIfAbsent(classLoader1, it -> new ConcurrentHashMap<>())
                    .computeIfAbsent(target, new Function<Class<?>, ServiceProvider>() {
                        @Override
                        public ServiceProvider apply(Class<?> aClass) {
                            ServiceProvider<T> serviceProvider = new ServiceProvider<>(target, classLoader, serviceAutowire);
                            serviceProvider.finders.addAll(finders);
                            serviceProvider.afterPropertiesSet();
                            return serviceProvider;
                        }
                    });
        }
    }
}
