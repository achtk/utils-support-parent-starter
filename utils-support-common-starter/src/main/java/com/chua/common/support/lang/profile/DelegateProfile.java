package com.chua.common.support.lang.profile;


import com.chua.common.support.bean.BeanBinder;
import com.chua.common.support.constant.ValueMode;
import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.context.environment.EnvironmentListener;
import com.chua.common.support.context.environment.property.PropertySource;
import com.chua.common.support.context.factory.ApplicationContextConfiguration;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.resolver.ProfileResolver;
import com.chua.common.support.lang.profile.value.MapProfileValue;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.placeholder.PlaceholderSupport;
import com.chua.common.support.placeholder.StringValuePropertyResolver;
import com.chua.common.support.spi.ServiceFactory;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.SYSTEM;


/**
 * 配置
 *
 * @author CH
 */
public class DelegateProfile implements  Profile, ServiceFactory<ProfileResolver> {

    private final Set<ProfileValue> profiles = new LinkedHashSet<>();
    private final Map<String, ProfileValue> profileMap = new ConcurrentHashMap<>();
    StringValuePropertyResolver propertyResolver = new StringValuePropertyResolver(new PlaceholderSupport().setResolver(this));

    @Override
    public Profile addProfile(Profile profile) {
        Map<String, ProfileValue> profile1 = getProfile();
        profileMap.putAll(profile1);
        for (ProfileValue profileValue : profile1.values()) {
            addProfileValue(profileValue.getName(), profileValue);
        }
        return this;
    }

    @Override
    public Profile addProfile(String resourceUrl) {
        ProfileResolver profileResolver = getResolver(resourceUrl);
        List<ProfileValue> resolve = profileResolver.resolve(resourceUrl);
        for (ProfileValue profileValue : resolve) {
            profiles.add(profileValue);
            addProfileValue(profileValue.getName(), profileValue);
        }
        return this;
    }

    /**
     * 解析其
     *
     * @param resourceUrl 地址
     * @return 结果
     */
    private ProfileResolver getResolver(String resourceUrl) {
        String suffix = resourceUrl;
        ProfileResolver profileResolver = null;
        while (!StringUtils.isNullOrEmpty(suffix = FileUtils.getSimpleExtension(suffix))) {
            profileResolver = getExtension(suffix);
            if (null != profileResolver) {
                break;
            }
        }
        return profileResolver;
    }

    @Override
    public Profile addProfile(int index, String resourceUrl) {
        ProfileResolver profileResolver = getResolver(resourceUrl);
        List<ProfileValue> resolve = profileResolver.resolve(resourceUrl);
        List<ProfileValue> tpl = new LinkedList<>(profiles);
        for (ProfileValue profileValue : resolve) {
            tpl.add(index, profileValue);
            addProfileValue(profileValue.getName(), profileValue);
        }
        profiles.clear();
        profiles.addAll(tpl);
        return this;
    }

    private ProfileValue addProfileValue(String name, ProfileValue profileValue) {
        if(null == profileValue) {
            ProfileValue profileValue1 = MapUtils.getComputeIfAbsent(profileMap, name, new MapProfileValue(name));
            profileMap.put(name, profileValue1);
            return profileValue1;
        }
        profileMap.put(name, profileValue);
        return profileValue;
    }

    @Override
    public synchronized Profile addProfile(String profile, String key, Object value) {
        profile = StringUtils.defaultString(profile, SYSTEM);
        ProfileValue profileValue1 = addProfileValue(profile, null);
        profiles.add(profileValue1);
        profileValue1.add(key, value);
        return this;
    }

    @Override
    public boolean noConfiguration() {
        return profiles.isEmpty();
    }

    @Override
    public <E> E bind(String pre, Class<E> target) {
        return BeanBinder.of(this::getObject).bind(pre, target).getValue();
    }

    @Override
    public Map<String, ProfileValue> getProfile() {
        return profileMap;
    }

    @Override
    public <T> T getType(String name, T defaultValue, Class<T> returnType) {
        return Converter.convertIfNecessary(Optional.ofNullable(getObject(name)).orElse(defaultValue), returnType);
    }

    @Override
    public Object getObject(String name, ValueMode valueMode) {
        for (ProfileValue profileValue : profiles) {
            if (!profileValue.contains(name, valueMode)) {
                continue;
            }

            return profileValue.getValue(name, valueMode);
        }
        return null;
    }

    @Override
    public String resolvePlaceholders(String name) {
        return propertyResolver.resolvePlaceholders(name);
    }

    @Override
    public String getProperty(String name) {
        return getString(name);
    }

    @Override
    public Environment contextConfiguration(ApplicationContextConfiguration contextConfiguration) {
        return this;
    }

    @Override
    public Environment addPropertySource(String name, PropertySource propertySource) {
        profileMap.put(name, new ProfileValue() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Object getValue(String name, ValueMode valueMode) {
                return propertySource.getProperty(name);
            }

            @Override
            public boolean contains(String name, ValueMode valueMode) {
                return propertySource.getProperty(name) != null;
            }

            @Override
            public void add(ProfileValue value) {

            }

            @Override
            public Set<String> keys() {
                return Collections.emptySet();
            }

            @Override
            public ProfileValue add(String s, Object o) {
                return this;
            }
        });
        return this;
    }

    @Override
    public Environment addListener(EnvironmentListener listener) {
        return this;
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public Class<ProfileResolver> getType() {
        return ProfileResolver.class;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return null;
    }
}
