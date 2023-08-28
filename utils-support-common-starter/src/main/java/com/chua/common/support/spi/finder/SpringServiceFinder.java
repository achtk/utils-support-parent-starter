package com.chua.common.support.spi.finder;

import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.spi.ServiceDefinition;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.chua.common.support.spi.autowire.AutoServiceAutowire.UTILS;


/**
 * 自定义spi查找器
 *
 * @author CH
 */
@Slf4j
public class SpringServiceFinder extends AbstractServiceFinder {

    private static final MethodDescribeProvider METHOD_DESCRIBE;

    static  {
        TypeDescribe typeDescribe = new TypeDescribe(UTILS);
        METHOD_DESCRIBE = typeDescribe.getMethodDescribe("getApplicationContext")
                .isChain().getMethodDescribe("getBeansOfType");
    }

    @Override
    protected List<ServiceDefinition> find() {
        List<ServiceDefinition> rs = new LinkedList<>();
        Map map = METHOD_DESCRIBE.executeSelf(Map.class, service);
        for (Object o : map.values()) {
            rs.addAll(buildDefinition(o));
        }
        return rs;
    }
}
