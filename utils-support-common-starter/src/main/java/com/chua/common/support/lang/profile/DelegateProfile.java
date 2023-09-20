package com.chua.common.support.lang.profile;


import com.chua.common.support.bean.BeanBinder;
import com.chua.common.support.constant.ValueMode;
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
import com.chua.common.support.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.SYSTEM;
import static com.chua.common.support.constant.NameConstant.FILE;


/**
 * 配置
 *
 * @author CH
 */
@Slf4j
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
    public Profile addProfile(URL url) {
        String fileName = null;
        String s = url.toExternalForm();
        if(FILE.equals(url.getProtocol())) {
            fileName = FileUtils.getName(s);
        } else {
            try {
                fileName = UrlUtils.getFileName(url.openConnection());
            } catch (IOException e) {
                log.error(e.getMessage());
                return this;
            }
        }
        ProfileResolver profileResolver = getResolver(fileName);
        if(null == profileResolver) {
            return this;
        }
        List<ProfileValue> resolve;
        try (InputStream inputStream = url.openStream()) {
            resolve = profileResolver.resolve(s, inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            return this;
        }
        for (ProfileValue profileValue : resolve) {
            profiles.add(profileValue);
            addProfileValue(profileValue.getName(), profileValue);
        }
        return this;
    }

    @Override
    public Profile addProfile(String resourceUrl) {
        ProfileResolver profileResolver = getResolver(resourceUrl);
        if(null == profileResolver) {
            return this;
        }
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
        if(null == profileResolver) {
            return this;
        }
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
    public void refresh(String file) {
        addProfile(file);
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
    public Class<ProfileResolver> getType() {
        return ProfileResolver.class;
    }

    @Override
    public String resolvePlaceholder(String placeholderName) {
        return null;
    }

    public Map<String, Object> asMap(String profileName) {
        ProfileValue profileValue = profileMap.get(profileName);
        if(null != profileValue) {
            Map<String, Object> rs = new LinkedHashMap<>();
            for (String key : profileValue.keys()) {
                rs.put(key, profileValue.getValue(key));
            }
            return rs;
        }
        return Collections.emptyMap();
    }


}
