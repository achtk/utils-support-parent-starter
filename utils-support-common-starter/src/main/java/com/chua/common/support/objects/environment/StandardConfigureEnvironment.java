package com.chua.common.support.objects.environment;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.objects.environment.properties.PropertySource;
import com.chua.common.support.objects.environment.resolver.PropertySourceResolver;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * 配置环境
 *
 * @author CH
 * @date 2023/09/01
 */
public class StandardConfigureEnvironment implements ConfigureEnvironment, InitializingAware {

    private final EnvironmentConfiguration configuration;
    private final List<PropertySource> profileValues = new LinkedList<>();

    public StandardConfigureEnvironment(EnvironmentConfiguration configuration) {
        this.configuration = configuration;
        afterPropertiesSet();
    }

    @Override
    public Object get(String name) {
        return null;
    }

    @Override
    public ConfigureEnvironment getEnvironment(EnvironmentConfiguration configuration) {
        return new StandardConfigureEnvironment(configuration);
    }

    @Override
    public void afterPropertiesSet() {
        String componentScan = configuration.componentScan();
        if(StringUtils.isEmpty(componentScan)) {
            return;
        }
        List<Metadata> metadata =
                Repository.of("classpath:" + componentScan)
                .add(Repository.of(new File(componentScan)))
                .getMetadata("*.*");
        for (Metadata metadatum : metadata) {
            URL url = metadatum.toUrl();
            try {
                String fileName = UrlUtils.getFileName(url.openConnection());
                try(InputStream inputStream = url.openStream()) {
                    PropertySourceResolver profileResolver = ServiceProvider.of(PropertySourceResolver.class).getNewExtension(FileUtils.getExtension(fileName), url.toExternalForm(), inputStream);
                    PropertySource propertySource = profileResolver.get();
                    if(null == propertySource) {
                        continue;
                    }
                    profileValues.add(propertySource);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
