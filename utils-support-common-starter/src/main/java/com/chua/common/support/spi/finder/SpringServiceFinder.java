package com.chua.common.support.spi.finder;

import com.chua.common.support.constant.NumberConstant;
import com.chua.common.support.reflection.describe.TypeDescribe;
import com.chua.common.support.reflection.describe.provider.MethodDescribeProvider;
import com.chua.common.support.resource.resource.UrlResource;
import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.spi.autowire.AutoServiceAutowire;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.chua.common.support.spi.autowire.AutoServiceAutowire.UTILS;


/**
 * 自定义spi查找器
 *
 * @author CH
 */
@Slf4j
public class SpringServiceFinder extends AbstractServiceFinder {

    private static final MethodDescribeProvider methodDescribe;

    static  {
        TypeDescribe typeDescribe = new TypeDescribe(UTILS);
        methodDescribe = typeDescribe.getMethodDescribe("getApplicationContext")
                .isChain().getMethodDescribe("getBeansOfType");
    }

    @Override
    protected List<ServiceDefinition> find() {
        List<ServiceDefinition> rs = new LinkedList<>();
        Map map = methodDescribe.executeSelf(Map.class, service);
        for (Object o : map.values()) {
            rs.addAll(buildDefinition(o));
        }
        return rs;
    }
}
