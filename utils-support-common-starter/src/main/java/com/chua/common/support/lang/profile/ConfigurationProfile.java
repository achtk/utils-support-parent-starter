package com.chua.common.support.lang.profile;


import com.chua.common.support.bean.BeanBinder;
import com.chua.common.support.bean.ProfileHandler;
import com.chua.common.support.collection.KeyValue;
import com.chua.common.support.lang.profile.resolver.ProfileResolver;
import com.chua.common.support.lang.profile.value.MapProfileValue;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.spi.ServiceFactory;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.*;


/**
 * 配置
 *
 * @author CH
 */
public class ConfigurationProfile extends ServiceFactory<ProfileResolver> implements Profile {

    private final Map<String, ProfileValue> profileUrl = new ConcurrentHashMap<>();

    @Override
    public Profile addProfile(String resourceUrl) {
        String suffix = resourceUrl;
        ProfileResolver profileResolver = null;
        while (!StringUtils.isNullOrEmpty(suffix = FileUtils.getSimpleExtension(suffix))) {
            profileResolver = provider.getExtension(suffix);
            if (null != profileResolver) {
                break;
            }
        }

        if (null == profileResolver) {
            return this;
        }

        List<ProfileValue> resolve = profileResolver.resolve(resourceUrl);
        for (ProfileValue profileValue : resolve) {
            profileUrl.put(profileValue.getName(), profileValue);
        }
        return this;
    }

    @Override
    public <E> E bind(String pre, Class<E> target) {
        return BeanBinder.of(new ProfileHandler() {
            @Override
            public Object getProperty(String name) {
                return get(name);
            }
        }).bind(pre, target).getValue();
    }

    @Override
    public <E> E bind(Class<E> target) {
        return BeanBinder.of(new ProfileHandler() {
            @Override
            public Object getProperty(String name) {
                return get(name);
            }
        }).bind(target).getValue();
    }

    @Override
    public Object get(String key, Object defaultValue) {
        Object profile = getProfile(new KeyValue(key));
        if (null != profile) {
            return profile;
        }
        if (key.contains(SYMBOL_DOT)) {
            return Optional.ofNullable(getJsonProfile(StringUtils.startWithAppend(key, SYMBOL_XPATH))).orElse(defaultValue);
        }

        return Optional.ofNullable(profile).orElse(defaultValue);
    }

    /**
     * 获取配置
     *
     * @param keyValue key
     * @return 结果
     */
    private Object getProfile(KeyValue keyValue) {
        Object value = null;

        for (String k : keyValue) {
            ProfileValue profileValue = getProfileValue(k);
            if (null == profileValue) {
                continue;
            }

            Object value1 = profileValue.getValue(k);
            if (null != value1) {
                value = value1;
            }
        }

        return value;
    }

    /**
     * 获取配置项
     *
     * @param k key
     * @return 结果
     */
    private ProfileValue getProfileValue(String k) {
        for (ProfileValue profileValue : profileUrl.values()) {
            Object value1 = profileValue.getValue(k);
            if (null != value1) {
                return profileValue;
            }
        }

        return null;
    }

    /**
     * 获取json值
     *
     * @param newKey key
     * @return 结果
     */
    private Object getJsonProfile(String newKey) {
        Object value = null;
        for (ProfileValue profileValue : profileUrl.values()) {
            Object value1 = profileValue.getJsonValue(newKey);
            if (null != value1) {
                value = value1;
            }
        }

        return value;
    }

    @Override
    public int size() {
        return profileUrl.size();
    }

    @Override
    public boolean isEmpty() {
        return profileUrl.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return profileUrl.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return profileUrl.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return get(key.toString(), null);
    }

    @Override
    public Object put(String key, Object value) {
        profileUrl.computeIfAbsent(DEFAULT, it -> new MapProfileValue(DEFAULT)).add(key, value);
        return value;
    }

    @Override
    public Object remove(Object key) {
        return profileUrl.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        profileUrl.clear();
    }

    @Override
    public Set<String> keySet() {
        return profileUrl.keySet();
    }

    @Override
    public Collection<Object> values() {
        return Collections.emptySet();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return Collections.emptySet();
    }
}
