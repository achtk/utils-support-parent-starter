package com.chua.common.support.objects.environment;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.objects.environment.properties.ProfilePropertySource;
import com.chua.common.support.objects.environment.properties.PropertySource;
import com.chua.common.support.objects.environment.properties.SystemEnvPropertySource;
import com.chua.common.support.objects.environment.properties.SystemPropertySource;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
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
        for (PropertySource profileValue : profileValues) {
            Object property = profileValue.getProperty(name);
            if(null != property) {
                return property;
            }
        }
        return null;
    }

    @Override
    public ConfigureEnvironment getEnvironment(EnvironmentConfiguration configuration) {
        return new StandardConfigureEnvironment(configuration);
    }

    @Override
    public void afterPropertiesSet() {
        String componentScan = configuration.componentScan();
        profileValues.add(new SystemEnvPropertySource());
        profileValues.add(new SystemPropertySource());

        if(StringUtils.isEmpty(componentScan)) {
            return;
        }
        List<Metadata> metadata =
                Repository.of("classpath:" + componentScan)
                .add(Repository.of(new File(componentScan)))
                .getMetadata("*.*");

        Profile profile = Profile.newDefault();

        for (Metadata metadatum : metadata) {
            URL url = metadatum.toUrl();
            profile.addProfile(url);
        }
        profileValues.add(new ProfilePropertySource(profile));
    }
}
