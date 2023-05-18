package com.chua.common.support.spi.finder;

import com.chua.common.support.constant.NumberConstant;
import com.chua.common.support.database.jdbc.springsrc.utils.StringUtils;
import com.chua.common.support.resource.resource.UrlResource;
import com.chua.common.support.spi.ServiceDefinition;
import com.chua.common.support.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * 自定义spi查找器
 *
 * @author CH
 */
@Slf4j
public class SpringServiceFinder extends AbstractServiceFinder {

    public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";

    private static final AtomicBoolean status = new AtomicBoolean(false);
    private Map<String, List<String>> stringListMap;


    @Override
    protected List<ServiceDefinition> find() {
        if(!status.get()) {
            status.set(true);
            this.stringListMap = loadSpringFactories(getClassLoader());

        }

        List<String> strings = stringListMap.get(service.getTypeName());
        if(null == strings) {
            return Collections.emptyList();
        }


        List<ServiceDefinition> rs = new LinkedList<>();
        for (String string : strings) {
            Class<?> aClass = ClassUtils.forName(string, getClassLoader());
            if(null == aClass) {
                continue;
            }

            rs.addAll(buildDefinition(aClass));
        }

        return rs;
    }
    private static Map<String, List<String>> loadSpringFactories(ClassLoader classLoader) {
        Map<String, List<String>> result = new HashMap<>(NumberConstant.DEFAULT_INITIAL_CAPACITY);
        try {
            Enumeration<URL> urls = classLoader.getResources(FACTORIES_RESOURCE_LOCATION);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                UrlResource resource = new UrlResource(url);
                Properties properties = new Properties();
                try (InputStreamReader reader = new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String factoryTypeName = ((String) entry.getKey()).trim();
                    String[] factoryImplementationNames =
                            StringUtils.commaDelimitedListToStringArray((String) entry.getValue());
                    for (String factoryImplementationName : factoryImplementationNames) {
                        result.computeIfAbsent(factoryTypeName, key -> new ArrayList<>())
                                .add(factoryImplementationName.trim());
                    }
                }
            }

            // Replace all lists with unmodifiable lists containing unique elements
            result.replaceAll((factoryType, implementations) -> implementations.stream().distinct()
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load factories from location [" +
                    FACTORIES_RESOURCE_LOCATION + "]", ex);
        }
        return result;
    }
}
