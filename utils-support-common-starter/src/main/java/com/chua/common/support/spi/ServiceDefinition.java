package com.chua.common.support.spi;

import com.chua.common.support.function.NameAware;
import com.chua.common.support.spi.autowire.ServiceAutowire;
import com.chua.common.support.spi.finder.ServiceFinder;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.net.URL;

/**
 * 服务定有
 *
 * @author CH
 */
@Data
public class ServiceDefinition {
    /**
     * 扩展接口实现类名
     */
    protected Class<?> implClass;
    /**
     * 扩展名称
     */
    private String name;
    /**
     * 扩展点排序值，大的优先级高
     */
    private Integer order;
    /**
     * 文件位置
     */
    private URL url;
    /**
     * 加载的时间
     */
    private long loadTime;
    /**
     * 类加载器
     */
    private ClassLoader classLoader;
    /**
     * 接口名称
     */
    private Class<?> type;

    /**
     * spi default
     */
    private boolean isDefault;

    /**
     * 实体对象
     */
    private Object obj;
    /**
     * 是否被加载
     */
    private boolean isLoaded;
    /**
     * 初始化异常
     */
    private Throwable ex;
    /**
     * 解析器
     */
    private Class<? extends ServiceFinder> finderType;
    /**
     * 流程记录
     */
    private StackTraceElement[] stack;

    /**
     * 获取定义
     *
     * @param serviceAutowire 自动装配器
     * @param args            参数
     * @param <T>             类型
     * @return 实现
     */
    public <T> T newInstance(ServiceAutowire serviceAutowire, Object... args) {
        if(null == implClass) {
            return null;
        }

        Constructor<?>[] constructors = implClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if(constructor.getParameterCount() != args.length) {
                continue;
            }
            constructor.setAccessible(true);
            try {
                return (T) serviceAutowire.autowire(constructor.newInstance(args));
            } catch (Exception ignored) {
            }
        }

        try {
            return (T) implClass.newInstance();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 获取定义
     *
     * @param serviceAutowire 自动装配器
     * @param <T>             类型
     * @return 实现
     */
    public <T> T getObj(ServiceAutowire serviceAutowire) {
        if (!isLoaded && null == obj) {
            synchronized (this) {
                if (!isLoaded && null == obj) {
                    isLoaded = true;
                    try {
                        this.obj = implClass.newInstance();
                        serviceAutowire.autowire(obj);
                    } catch (Exception e) {
                        stack = Thread.currentThread().getStackTrace();
                        ex = e;
                    }
                }
            }
        }

        return (T) obj;
    }

    /**
     * 是否是NameAware子类
     *
     * @return 是否是NameAware子类
     */
    public boolean isPresent() {
        return NameAware.class.isAssignableFrom(implClass);
    }
}
