package com.chua.common.support.spi.finder;

import com.chua.common.support.annotations.*;
import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.spi.autowire.ServiceAutowire;
import com.chua.common.support.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.spi.autowire.AutoServiceAutowire.INSTANCE;

/**
 * 服务查找器
 *
 * @author CH
 */
@Slf4j
public abstract class AbstractServiceFinder implements ServiceFinder{
    private final Map<ClassLoader, Map<Class<?>, List<ServiceDefinition>>> CACHE = new ConcurrentHashMap<>();
    protected Class<?> service;
    private ClassLoader classLoader;
    protected ServiceAutowire serviceAutowire;

    @Override
    public List<ServiceDefinition> analyze(Class<?> service, ClassLoader classLoader) {
        this.classLoader = Optional.ofNullable(classLoader).orElse(ClassUtils.getDefaultClassLoader());
        if (CACHE.containsKey(this.classLoader) && CACHE.get(this.classLoader).containsKey(service)) {
            return CACHE.get(this.classLoader).get(service);
        }

        this.service = service;
        if (log.isTraceEnabled()) {
            log.trace("The system starts to query {} service links", service.getTypeName());
        }
        List<ServiceDefinition> serviceDefinitions = find();

        CACHE.computeIfAbsent(this.classLoader, it -> new ConcurrentHashMap<>()).putIfAbsent(service, serviceDefinitions);
        if (log.isTraceEnabled()) {
            log.trace("The {} service link is loaded successfully", service.getTypeName());
        }
        return serviceDefinitions;
    }

    /**
     * 初始化定义
     *
     * @param obj 实现
     * @return 定义
     */
    public List<ServiceDefinition> buildDefinition(Object obj) {
        return buildDefinition(obj, obj.getClass(), null, null);
    }

    /**
     * 初始化定义
     *
     * @param implType 实现类
     * @return 定义
     */
    public List<ServiceDefinition> buildDefinition(Class<?> implType) {
        return buildDefinition(null, implType, null, null);
    }

    /**
     * 初始化定义
     *
     * @param obj      实现
     * @param implType 实现类
     * @param alias    别名
     * @param url      地址
     * @return 定义
     */
    public List<ServiceDefinition> buildDefinition(Object obj, Class<?> implType, String alias, URL url) {
        if (implType.getDeclaredAnnotation(SpiIgnore.class) != null) {
            return Collections.emptyList();
        }

        if (implType.isEnum()) {
            return buildEnumDefinition(obj, implType, alias, url);
        }

        List<ServiceDefinition> rs = new LinkedList<>(buildDefinitionType(obj, implType, url));
        if (null == alias || "".equals(alias)) {
            return rs;
        }
        Order order = implType.getDeclaredAnnotation(Order.class);
        rs.add(buildDefinitionAlias(obj, implType, url, alias, null == order ? 0 : order.value()));
        return rs;
    }

    /**
     * 处理枚举类
     *
     * @param obj      对象
     * @param implType 实现类
     * @param alias    别名
     * @param url      地址
     * @return 结果
     */
    private List<ServiceDefinition> buildEnumDefinition(Object obj, Class<?> implType, String alias, URL url) {
        List<ServiceDefinition> rs = new LinkedList<>();
        if (service.isAssignableFrom(implType)) {
            rs.add(buildDefinitionAlias(obj, implType, url, alias, 0));
        }

        Object[] enumConstants = implType.getEnumConstants();
        for (Object enumConstant : enumConstants) {
            rs.addAll(buildDefinition(enumConstant, enumConstant.getClass(), alias, url));
        }

        return rs;
    }

    /**
     * 初始化定义
     *
     * @param obj      实现
     * @param implType 实现类
     * @param url      地址
     * @param alias    别名
     * @param order    优先级
     * @return 定义
     */
    @SuppressWarnings("ALL")
    private ServiceDefinition buildDefinitionAlias(Object obj, Class<?> implType, URL url, String alias, int order) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setClassLoader(getClassLoader());
        serviceDefinition.setLoadTime(System.currentTimeMillis());
        serviceDefinition.setOrder(order);
        serviceDefinition.setName(alias);
        serviceDefinition.setObj(obj);
        serviceDefinition.setFinderType(this.getClass());
        serviceDefinition.setImplClass(implType);
        serviceDefinition.setUrl(url);
        serviceDefinition.setType(service);
        serviceDefinition.setDefault(implType.isAnnotationPresent(SpiDefault.class));

        return serviceDefinition;
    }

    /**
     * 构建类型
     *
     * @param obj      实现
     * @param implType 实现类
     * @return 定义
     */
    private Collection<? extends ServiceDefinition> buildDefinitionType(Object obj, Class<?> implType) {
        return buildDefinitionType(obj, implType, null);
    }

    /**
     * 构建类型
     *
     * @param obj      实现
     * @param implType 实现类
     * @param url      地址
     * @return 定义
     */
    private Collection<? extends ServiceDefinition> buildDefinitionType(Object obj, Class<?> implType, URL url) {
        if (!isCondition(implType)) {
            return Collections.emptyList();
        }

        String[] name = getName(implType);
        if (name.length == 0) {
            return Collections.emptyList();
        }
        List<ServiceDefinition> rs = new LinkedList<>();
        url = url == null ? implType.getProtectionDomain().getCodeSource().getLocation() : url;
        int order = getOrder(implType);
        for (String s : name) {
            rs.add(buildDefinitionAlias(obj, implType, url, s, order));
        }

        return rs;
    }

    /**
     * 判断条件是否存在
     *
     * @param implType 实现类
     * @return 条件是否存在
     */
    private boolean isCondition(Class<?> implType) {
        SpiIgnore spiIgnore = implType.getDeclaredAnnotation(SpiIgnore.class);
        if (null != spiIgnore) {
            return false;
        }

        SpiCondition spiCondition = implType.getDeclaredAnnotation(SpiCondition.class);
        if (null == spiCondition) {
            return true;
        }

        String[] value = spiCondition.value();
        for (String s : value) {
            try {
                Class.forName(s);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        Class<? extends SpiCondition.Condition>[] aClass = spiCondition.onCondition();
        for (Class<? extends SpiCondition.Condition> aClass1 : aClass) {
            SpiCondition.Condition condition = null;
            try {
                condition = aClass1.newInstance();
            } catch (Exception ignored) {
            }

            if(null != condition && !condition.isCondition()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取优先级
     *
     * @param implType 实现类
     * @return 名称
     */
    private int getOrder(Class<?> implType) {
        Spi spi = implType.getDeclaredAnnotation(Spi.class);
        if (null != spi) {
            return spi.order();
        }

        Order order = implType.getDeclaredAnnotation(Order.class);
        if (null != order) {
            return order.value();
        }

        return 0;
    }

    /**
     * 获取名称
     *
     * @param implType 实现类
     * @return 名称
     */
    private String[] getName(Class<?> implType) {
        Set<String> name = new LinkedHashSet<>();
        Spi spi = implType.getDeclaredAnnotation(Spi.class);
        if (null != spi) {
            name.addAll(Arrays.asList(spi.value()));
        }
        Extension extension = implType.getDeclaredAnnotation(Extension.class);
        if (null != extension) {
            name.add(extension.value());
        }

        return name.toArray(new String[0]);
    }

    /**
     * 类加载器
     *
     * @return 类加载器
     */
    public ClassLoader getClassLoader() {
        return Optional.ofNullable(classLoader).orElse(ClassUtils.getDefaultClassLoader());
    }

    /**
     * 接口名称
     *
     * @return 接口名称
     */
    public String getInterfaceName() {
        return service.getName();
    }

    /**
     * 查找实现
     *
     * @return 实现
     */
    protected abstract List<ServiceDefinition> find();
}
