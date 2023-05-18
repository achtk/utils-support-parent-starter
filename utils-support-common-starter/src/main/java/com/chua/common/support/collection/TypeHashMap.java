package com.chua.common.support.collection;

import com.chua.common.support.bean.BeanBinder;
import com.chua.common.support.bean.ProfileHandler;
import com.chua.common.support.constant.ValueMode;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.profile.value.MapProfileValue;
import com.chua.common.support.lang.profile.value.ProfileValue;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;

import static com.chua.common.support.constant.CommonConstant.SYSTEM;

/**
 * 类型集合
 *
 * @author CH
 * @version 1.0.0
 */
public class TypeHashMap implements Profile {

    private final Map<String, ProfileValue> map = new LinkedHashMap<>();
    private final ProfileValue profileValue = new MapProfileValue(SYSTEM);

    {
        map.put(SYSTEM, profileValue);
    }

    public TypeHashMap(Map<?, ?> map) {
        map.forEach((k, v) -> profileValue.add(k.toString(), v));
    }

    public TypeHashMap() {
    }

    @Override
    public Profile addProfile(Profile profile) {
        Map<String, ProfileValue> profile1 = profile.getProfile();
        for (ProfileValue value : profile1.values()) {
            profileValue.add(value);
        }
        return this;
    }

    @Override
    public Profile addProfile(String resourceUrl) {
        return this;
    }

    @Override
    public Profile addProfile(int index, String resourceUrl) {
        return this;
    }

    @Override
    public Profile addProfile(String profile, String key, Object value) {
        profileValue.add(key, value);
        return this;
    }

    @Override
    public Object getObject(String name, ValueMode valueMode) {
        return profileValue.getValue(name, valueMode);
    }

    @Override
    public boolean noConfiguration() {
        return false;
    }

    @Override
    public <E> E bind(String pre, Class<E> target) {
        return BeanBinder.of(new ProfileHandler() {
            @Override
            public Object getProperty(String name) {
                return profileValue.getValue(name);
            }
        }).bind(pre, target).getValue();
    }

    @Override
    public Map<String, ProfileValue> getProfile() {
        return map;
    }

    @Override
    public <T> T getType(String name, T defaultValue, Class<T> returnType) {
        return Converter.convertIfNecessary(Optional.ofNullable(getObject(name)).orElse((Object)defaultValue), returnType);
    }
}
