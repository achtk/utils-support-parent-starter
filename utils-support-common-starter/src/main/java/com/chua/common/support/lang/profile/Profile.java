package com.chua.common.support.lang.profile;


import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.placeholder.PlaceholderResolver;

import java.net.URL;
import java.util.*;

/**
 * 配置
 *
 * @author CH
 */
public interface Profile extends PlaceholderResolver, ProfileReliable {
    /**
     * 初始化
     *
     * @return Profile
     */
    static Profile newDefault() {
        return new DelegateProfile();
    }

    /**
     * 添加配置
     *
     * @param profile 配置目录
     * @return this
     */
    Profile addProfile(Profile profile);

    /**
     * 添加配置
     *
     * @param url 配置目录
     * @return this
     */
    Profile addProfile(URL url);

    /**
     * 添加配置
     *
     * @param resourceUrl 配置目录
     * @return this
     */
    Profile addProfile(String resourceUrl);

    /**
     * 添加配置
     *
     * @param index       索引
     * @param resourceUrl 配置目录
     * @return this
     */
    Profile addProfile(int index, String resourceUrl);

    /**
     * 添加数据
     *
     * @param profile 配置文件
     * @param key     key
     * @param value   value
     * @return this
     */
    Profile addProfile(String profile, String key, Object value);

    /**
     * 添加数据
     *
     * @param value value
     * @return this
     */
    default Profile addProfile(Map<String, Object> value) {
        value.forEach(this::addProfile);
        return this;
    }

    /**
     * 添加数据
     *
     * @param profile 配置文件
     * @param value   value
     * @return this
     */
    default Profile addProfile(String profile, Map<String, Object> value) {
        value.forEach((k, v) -> addProfile(profile, k, v));
        return this;
    }

    /**
     * 添加数据
     *
     * @param key   key
     * @param value value
     * @return this
     */
    default Profile addProfile(String key, Object value) {
        return addProfile(null, key, value);
    }

    /**
     * 添加数据
     *
     * @param row   column
     * @param key   key
     * @param value value
     * @return this
     */
    default Profile addMapProfile(String row, String key, Object value) {
        Object o = getObject(row);
        if (null == o) {
            Map<String, Object> tpl = new LinkedHashMap<>();
            tpl.put(key, value);
            addProfile(row, tpl);
            return this;
        }


        if (o instanceof Map) {
            ((Map) o).put(key, value);
            return this;
        }

        return this;
    }

    /**
     * 添加数据
     *
     * @param row   column
     * @param value value
     * @return this
     */
    default Profile addListProfile(String row, Object value) {
        Object o = getObject(row);
        if (null == o) {
            List<Object> tpl = new LinkedList<>();
            tpl.add(value);
            addProfile(row, tpl);
            return this;
        }


        if (o instanceof Collection) {
            List<Object> tpl = new LinkedList<>((Collection<?>) o);
            tpl.add(value);
            addProfile(row, tpl);
            return this;
        }

        return this;
    }


    /**
     * 占位符处理
     * @param name 值
     * @return 值
     */
    String resolvePlaceholders(String name);

    /**
     * 是否无配置
     *
     * @return 是否无配置
     */
    boolean noConfiguration();

    /**
     * 对象绑定
     *
     * @param pre    前缀
     * @param target 目标类型
     * @param <E>    类型
     * @return E
     */
    <E> E bind(String pre, Class<E> target);

    /**
     * 对象绑定
     *
     * @param pre    前缀
     * @param target 目标类型
     * @param <E>    类型
     * @return E
     */
    default <E> E bind(String[] pre, Class<E> target) {
        Map<String, Object> tpl = new LinkedHashMap<>();
        for (String s : pre) {
            E bind = bind(s, target);
            tpl.putAll(BeanMap.of(bind, true));
        }

        return BeanUtils.copyProperties(tpl, target);
    }

    /**
     * 对象绑定
     *
     * @param target 目标类型
     * @param <E>    类型
     * @return E e
     */
    default <E> E bind(Class<E> target) {
        return bind("", target);
    }


    /**
     * 获取配置
     *
     * @return this
     */
    Map<String, ProfileValue> getProfile();
    /**
     * 获取值
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @param returnType   返回类型
     * @return T
     */
    <T> T getType(String name, T defaultValue, Class<T> returnType);
    /**
     * 获取类型
     * @param name 名称
     * @param targetType 目标类型
     * @return 结果
     */
    @SuppressWarnings("ALL")
    default Class<?> getForType(String name) {
        return getForType(name, Object.class);
    }
    /**
     * 获取类型
     * @param name 名称
     * @param targetType 目标类型
     * @return 结果
     * @param <T> 类型
     */
    @SuppressWarnings("ALL")
    default <T>Class<T> getForType(String name, Class<T> targetType) {
        Object object = getObject(name);
        if(null == object ) {
            return null;
        }

        if(object instanceof Class<?> && targetType.isAssignableFrom((Class<?>) object)) {
            return (Class<T>) object;
        }

        Class<?> aClass = object.getClass();
        if(targetType.isAssignableFrom(aClass)) {
            return (Class<T>) aClass;
        }

        return null;
    }
}
