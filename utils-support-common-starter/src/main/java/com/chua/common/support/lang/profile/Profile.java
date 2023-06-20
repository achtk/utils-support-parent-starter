package com.chua.common.support.lang.profile;


import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.constant.ValueMode;
import com.chua.common.support.context.environment.Environment;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.placeholder.PlaceholderResolver;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 配置
 *
 * @author CH
 */
public interface Profile extends Environment, PlaceholderResolver {
    /**
     * 初始化
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
     * 获取数据
     *
     * @param name      名称
     * @param valueMode 取值模式
     * @return 结果
     */
    Object getObject(String name, ValueMode valueMode);

    /**
     * 占位符处理
     * @param name 值
     * @return 值
     */
    String resolvePlaceholders(String name);
    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Object getObject(String name) {
        return getObject(name, ValueMode.XPATH);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default Object getObject(String name, Object defaultValue) {
        return Optional.ofNullable(getObject(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default String getString(String name) {
        return Converter.convertIfNecessary(getObject(name), String.class);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default String getString(String... name) {
        for (String s : name) {
            Object object = getObject(s);
            if (null != object) {
                return Converter.convertIfNecessary(object, String.class);
            }
        }
        return null;
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default String getString(String name, String defaultValue) {
        return Optional.ofNullable(getString(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Double getDouble(String name) {
        return Converter.convertIfNecessary(getObject(name), Double.class);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default double getDoubleValue(String name, double defaultValue) {
        return Optional.ofNullable(getDouble(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default double getDoubleValue(String name) {
        return getDoubleValue(name, 0d);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Float getFloat(String name) {
        return Converter.convertIfNecessary(getObject(name), Float.class);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default float getFloatValue(String name, float defaultValue) {
        return Optional.ofNullable(getFloat(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default float getFloatValue(String name) {
        return getFloatValue(name, 0f);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Long getLong(String name) {
        return Converter.convertIfNecessary(getObject(name), Long.class);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default long getLongValue(String name, long defaultValue) {
        return Optional.ofNullable(getLong(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default long getLongValue(String name) {
        return getLongValue(name, 0L);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Integer getInteger(String name) {
        return Converter.convertIfNecessary(getObject(name), Integer.class);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default int getIntValue(String name, int defaultValue) {
        return Optional.ofNullable(getInteger(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default long getIntValue(String name) {
        return getIntValue(name, 0);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Short getShort(String name) {
        return Converter.convertIfNecessary(getObject(name), Short.class);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default short getShortValue(String name, short defaultValue) {
        return Optional.ofNullable(getShort(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default short getShortValue(String name) {
        return getShortValue(name, (short) 0);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Byte getByte(String name) {
        return Converter.convertIfNecessary(getObject(name), Byte.class);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default byte getByteValue(String name, byte defaultValue) {
        return Optional.ofNullable(getByte(name)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default byte getByteValue(String name) {
        return getByteValue(name, (byte) 0);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default Date getDate(String name, Date defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getObject(name), Date.class)).orElse(defaultValue);
    }


    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Date getDate(String name) {
        return getDate(name, null);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default BigDecimal getBigDecimal(String name, BigDecimal defaultValue) {
        return Optional.ofNullable(Converter.convertIfNecessary(getObject(name), BigDecimal.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default BigDecimal getBigDecimal(String name) {
        return getBigDecimal(name, null);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default Boolean getBoolean(String name) {
        Object object = getObject(name);
        return Converter.convertIfNecessary(object, Boolean.class);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default boolean getBooleanValue(String name) {
        return getBooleanValue(name, false);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default boolean getBooleanValue(String name, boolean defaultValue) {
        Object object = getObject(name);
        return Optional.ofNullable(Converter.convertIfNecessary(object, Boolean.class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default JsonObject getJsonObject(String name) {
        Object object = getObject(name);
        return new JsonObject(BeanMap.of(object, true));
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default JsonArray getJsonArray(String name) {
        Object object = getObject(name);
        if (object instanceof Collection) {
            return new JsonArray((Collection) object);
        }
        return null;
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    default String[] getStringArray(String name) {
        return getStringArray(name, null);
    }

    /**
     * 获取数据
     *
     * @param name         名称
     * @param defaultValue 默认值
     * @return 结果
     */
    default String[] getStringArray(String name, String[] defaultValue) {
        Object object = getObject(name);
        return Optional.ofNullable(Converter.convertIfNecessary(object, String[].class)).orElse(defaultValue);
    }

    /**
     * 获取数据
     *
     * @param name   名称
     * @param target 类型
     * @return 结果
     */
    @SuppressWarnings("ALL")
    default <T> List<T> getList(String name, Class<T> target) {
        Object object = getObject(name);
        if (null == object) {
            return Collections.emptyList();
        }

        if (object instanceof Collection) {
            return ((Collection<?>) object).stream()
                    .map(it -> Converter.convertIfNecessary(it, target)).filter(Objects::nonNull).collect(Collectors.toList());
        }

        if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            List<T> rs = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                T convertIfNecessary = Converter.convertIfNecessary(Array.get(object, i), target);
                if (null == convertIfNecessary) {
                    continue;
                }
                rs.add(convertIfNecessary);
            }

            return rs;
        }

        List list = Converter.convertIfNecessary(object, List.class);
        if (null != list) {
            return (List<T>) list.stream()
                    .map(it -> Converter.convertIfNecessary(it, target)).filter(Objects::nonNull).collect(Collectors.toList());
        }

        return (List<T>) Collections.singletonList(Converter.convertIfNecessary(object, target));
    }

    /**
     * 获取数据
     *
     * @param name   名称
     * @param target 类型
     * @return 结果
     */
    @SuppressWarnings("ALL")
    default <T> Set<T> getSet(String name, Class<T> target) {
        Object object = getObject(name);
        if (null == object) {
            return Collections.emptySet();
        }

        if (object instanceof Collection) {
            return ((Collection<?>) object).stream()
                    .map(it -> Converter.convertIfNecessary(it, target)).filter(Objects::nonNull).collect(Collectors.toSet());
        }

        if (object.getClass().isArray()) {
            int length = Array.getLength(object);
            Set<T> rs = new HashSet<>(length);
            for (int i = 0; i < length; i++) {
                T convertIfNecessary = Converter.convertIfNecessary(Array.get(object, i), target);
                if (null == convertIfNecessary) {
                    continue;
                }
                rs.add(convertIfNecessary);
            }

            return rs;
        }

        Set set = Converter.convertIfNecessary(object, Set.class);
        if (null != set) {
            return (Set<T>) set.stream()
                    .map(it -> Converter.convertIfNecessary(it, target)).filter(Objects::nonNull).collect(Collectors.toSet());
        }

        return (Set<T>) Collections.singleton(Converter.convertIfNecessary(object, target));
    }

    /**
     * 获取数据
     *
     * @param name 名称
     * @return 结果
     */
    @SuppressWarnings("ALL")
    default <T> List<T> getList(String name) {
        return (List<T>) getList(name, Object.class);
    }

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
