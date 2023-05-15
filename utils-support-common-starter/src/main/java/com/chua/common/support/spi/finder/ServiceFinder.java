package com.chua.common.support.spi.finder;

import com.chua.common.support.spi.ServiceDefinition;

import java.util.List;

/**
 * 服务查找器
 *
 * @author CH
 */
public interface ServiceFinder {

    /**
     * Spi机制解析对象
     *
     * @param service     接口
     * @param classLoader 类加载器
     * @return Multimap
     */
    List<ServiceDefinition> analyze(Class<?> service, ClassLoader classLoader);

}
