package com.chua.common.support.objects.environment;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.profile.resolver.PropertiesProfileResolver;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

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
public class ConfigureEnvironment implements ObjectEnvironment, InitializingAware {

    private final EnvironmentConfiguration configuration;
    private final List<ProfileValue> profileValues = new LinkedList<>();

    public ConfigureEnvironment(EnvironmentConfiguration configuration) {
        this.configuration = configuration;
        afterPropertiesSet();
    }

    @Override
    public Object get(String name) {
        return null;
    }

    @Override
    public ObjectEnvironment getEnvironment(EnvironmentConfiguration configuration) {
        return new ConfigureEnvironment(configuration);
    }

    @Override
    public void afterPropertiesSet() {
        String componentScan = configuration.componentScan();
        if(StringUtils.isEmpty(componentScan)) {
            return;
        }
        List<Metadata> metadata = Repository.current().add(Repository.classpath()).getMetadata("*.*");
        for (Metadata metadatum : metadata) {
            URL url = metadatum.toUrl();
            try {
                String fileName = UrlUtils.getFileName(url.openConnection());
                PropertiesProfileResolver profileResolver = ServiceProvider.of(PropertiesProfileResolver.class).getExtension(FileUtils.getExtension(fileName));
                try (InputStream inputStream = url.openStream()) {
                    List<ProfileValue> resolve = profileResolver.resolve(url.toExternalForm(), inputStream);
                    if(CollectionUtils.isEmpty(resolve)) {
                        continue;
                    }
                    profileValues.addAll(resolve);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
