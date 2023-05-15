package com.chua.common.support.collection;

import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.utils.MapUtils;
import com.google.common.base.Strings;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_ASTERISK;


/**
 * 类型集合
 *
 * @author CH
 * @version 1.0.0
 */
public interface TypeMap<E> extends Map<String, Object> {
    /**
     * 添加数据
     *
     * @param row  row
     * @param item 值
     * @return this
     */
    @SuppressWarnings("all")
    default E addMapList(String row, List<Object> item) {
        for (Object o : item) {
            addMapList(row, o);
        }
        return (E) this;
    }

    /**
     * 添加数据
     *
     * @param row  row
     * @param item 值
     * @return this
     */
    @SuppressWarnings("all")
    default E addMapList(String row, Object item) {
        if (item instanceof Collection) {
            ((Collection<?>) item).forEach(it -> {
                addList(row, BeanMap.of(it, true));
            });
            return (E) this;
        }
        return (E) addList(row, BeanMap.of(item, true));
    }

    /**
     * 添加数据
     *
     * @param row  row
     * @param item 值
     * @return this
     */
    @SuppressWarnings("all")
    default E addList(String row, List<Map<String, Object>> item) {
        for (Map<String, Object> stringObjectMap : item) {
            addList(row, stringObjectMap);
        }
        return (E) this;
    }

    /**
     * 添加数据
     *
     * @param row  row
     * @param item 值
     * @return this
     */
    @SuppressWarnings("all")
    default E addList(String row, Map<String, Object> item) {
        Object computeIfPresent = source().computeIfAbsent(row, it1 -> new LinkedList<>());
        if (computeIfPresent instanceof List) {
            ((List) computeIfPresent).add(item);
            return (E) this;
        }

        source().remove(row);
        return addList(row, item);
    }

    /**
     * 添加数据
     *
     * @param row   row
     * @param key   索引
     * @param value 值
     * @return this
     */
    @SuppressWarnings("all")
    default E add(String row, String key, Object value) {
        if (null != source()) {
            Object o = source().get(row);
            if (null == o) {
                Map<String, Object> tpl = new LinkedHashMap<>();
                tpl.put(key, value);
                source().put(row, tpl);
                return (E) this;
            }


            if(o instanceof Map) {
                ((Map) o).put(key, value);
                return (E) this;
            }

            source().remove(row);
            return add(row, key, value);
        }
        return (E) this;
    }
    /**
     * 添加数据
     *
     * @param key   索引
     * @param value 值
     * @return this
     */
    @SuppressWarnings("all")
    default E add(String key, Object value) {
        if (null != source()) {
            Object o = source().get(key);
            if (null == o) {
                put(key, value);
            } else if (!(o instanceof List)) {
                List<Object> result = new ArrayList<>();
                result.add(o);
                result.add(value);
                put(key, result);
            } else {
                List result = (List) o;
                result.add(o);
                result.add(value);
                put(key, result);
            }
        }
        return (E) this;
    }

    /**
     * 清除
     */
    @Override
    default void clear() {
        if (null != source()) {
            source().clear();
        }
    }

    /**
     * key对应的value是否存在, 存在返回值, 不存在存储value并返回key的值
     *
     * @param key   索引
     * @param value 值
     * @param type  返回类型
     * @param <T>   元素类型
     * @return 值
     */
    @SuppressWarnings("all")
    default <T> T computeIfAbsent(String key, Function<String, T> value, Class<T> type) {
        if (Strings.isNullOrEmpty(key) || null == source()) {
            return null;
        }

        if (source().containsKey(key)) {
            return Converter.convertIfNecessary(source().get(key), type);
        }

        source().put(key, value.apply(key));
        return (T) source().get(key);
    }

    /**
     * 是否包含索引
     *
     * @param key 索引
     * @return 是否包含索引
     */
    @Override
    default boolean containsKey(Object key) {
        return null != source() && source().containsKey(key);
    }

    /**
     * 是否包含有效
     * <p>value:String -> not in null, ""</p>
     * <p>value:Number -> not equals 0</p>
     * <p>value:Array -> length gt 0</p>
     * <p>value:Collection -> length gt 0</p>
     * <p>value:Map -> length gt 0</p>
     *
     * @param key 索引
     * @return 是否包含索引
     */
    default boolean containsVailValue(String key) {
        if (null == key) {
            return false;
        }

        Object object = getObject(key);
        if (null == object) {
            return false;
        }

        if (object instanceof String) {
            return !"".equals(object.toString().trim());
        }

        if (object instanceof Number) {
            return 0 != ((Number) object).intValue();
        }

        if (object instanceof Collection) {
            return !((Collection) object).isEmpty();
        }

        if (object instanceof Map) {
            return !((Map) object).isEmpty();
        }
        Class<?> aClass = object.getClass();
        if (aClass.isArray()) {
            return ((Object[]) object).length > 0;
        }

        if (aClass.isPrimitive()) {
            return new BigDecimal(object.toString()).intValue() != 0;
        }
        return true;
    }

    /**
     * 是否包含索引
     *
     * @param value 值
     * @return 是否包含索引
     */
    @Override
    default boolean containsValue(Object value) {
        return null != source() && source().containsValue(value);
    }

    /**
     * 遍历
     *
     * @return set
     */
    @Override
    default Set<Entry<String, Object>> entrySet() {
        return null != source() ? source().entrySet() : Collections.emptySet();
    }


    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    @Override
    default Object get(Object key) {
        if (null == key) {
            return null;
        }
        return getObject(key.toString());
    }

    /**
     * 获取值
     *
     * @param key  索引
     * @param type 类型
     * @return 值
     */
    default <T> T[] getArray(String key, Class<T[]> type) {
        Object object = getObject(key);
        if (null == object) {
            return (T[]) Array.newInstance(type.getComponentType(), 0);
        }

        if (object.getClass().isArray()) {
            return (T[]) object;
        }
        Object newInstance = Array.newInstance(type.getComponentType(), 1);
        Array.set(newInstance, 0, object);

        return (T[]) newInstance;
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Boolean getBoolean(String key, Boolean defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Boolean.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default boolean getBooleanValue(String key, boolean defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Boolean.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Byte getByte(String key, Byte defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Byte.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Byte getByte(String key) {
        return getByte(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default byte getByteValue(String key, byte defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Byte.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default byte getByteValue(String key) {
        return getByteValue(key, (byte) 0);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Date getDate(String key, Date defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Date.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Date getDate(String key) {
        return getDate(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Double getDouble(String key, Double defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Double.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Double getDouble(String key) {
        return getDouble(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default double getDoubleValue(String key, double defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Double.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default double getDoubleValue(String key) {
        return getDoubleValue(key, 0d);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Float getFloat(String key, Float defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Float.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Float getFloat(String key) {
        return getFloat(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default float getFloatValue(String key, float defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Float.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default float getFloatValue(String key) {
        return getFloatValue(key, 0f);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default int getIntValue(String key, int defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Integer.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Integer getInteger(String key, Integer defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Integer.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Integer getInteger(String key) {
        return getInteger(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default LocalDate getLocalDate(String key, LocalDate defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, LocalDate.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default LocalDate getLocalDate(String key) {
        return getLocalDate(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default LocalDateTime getLocalDateTime(String key, LocalDateTime defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, LocalDateTime.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default LocalDateTime getLocalDateTime(String key) {
        return getLocalDateTime(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default LocalTime getLocalTime(String key, LocalTime defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, LocalTime.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default LocalTime getLocalTime(String key) {
        return getLocalTime(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Long getLong(char key, Long defaultValue) {
        Object object = getObject(key + "");
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Long.class);
    }
    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Long getLong(String key, Long defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Long.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Long getLong(String key) {
        return getLong(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default long getLongValue(String key, long defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Long.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default long getLongValue(String key) {
        return getLongValue(key, 0);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    Object getObject(String key, Object defaultValue);

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Object getObject(String key) {
        return getObject(key, null);
    }


    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default List<Object> getList(String key) {
        Object object = getObject(key);
        return (null == object || !(object instanceof Collection)) ? Collections.emptyList() : new LinkedList<>((Collection<?>) object);
    }
    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Optional<Object> getOptionalObject(String key) {
        return Optional.ofNullable(getObject(key, null));
    }


    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default Short getShort(String key, Short defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, Short.class);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default Short getShort(String key) {
        return getShort(key, null);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default short getShortValue(String key, short defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Optional.ofNullable(Converter.convertIfNecessary(object, Short.class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default short getShortValue(String key) {
        return getShortValue(key, (short) 0);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default String getString(String key, String defaultValue) {
        Object object = getObject(key);
        return null == object ? defaultValue : Converter.convertIfNecessary(object, String.class);
    }

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default String getStringHasDefault(String defaultValue, String... key) {
        Object object = null;
        for (String s : key) {
            object = getObject(s);
            if(null != object) {
                return Converter.convertIfNecessary(object, String.class);
            }
        }
        return defaultValue;
    }
    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default String getString(String[] key, String defaultValue) {
        Object object = null;
        for (String s : key) {
            object = getObject(s);
            if(null != object) {
                return Converter.convertIfNecessary(object, String.class);
            }
        }
        return defaultValue;
    }
    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default String getString(Character key) {
        return getString(key + "", null);
    }
    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default String getString(String key) {
        return getString(key, null);
    }

    /**
     * 获取值
     *
     * @param key 索引
     * @return 值
     */
    default String[] getStringArray(String key) {
        Object object = getObject(key);
        return Converter.convertIfNecessary(object, String[].class);
    }
    //---------------------------------------------数据更新操作---------------------------------------------------

    /**
     * 获取值
     *
     * @param key          索引
     * @param defaultValue 默认值
     * @return 值
     */
    default String[] getStringArray(String key, String[] defaultValue) {
        Object object = getObject(key);
        return Optional.ofNullable(Converter.convertIfNecessary(object, String[].class)).orElse(defaultValue);
    }

    /**
     * 获取值
     *
     * @param type 类型
     * @param key  索引
     * @return 值
     */
    default <T> T getType(String key, Class<T> type) {
        Object object = getObject(key, null);
        if (null == object || type.isAssignableFrom(object.getClass())) {
            return (T) object;
        }
        return Converter.convertIfNecessary(object, type);
    }

    /**
     * 获取长度
     *
     * @return 长度
     */
    @Override
    default boolean isEmpty() {
        return null == source() || source().isEmpty();
    }

    /**
     * 获取长度
     *
     * @return 长度
     */
    @Override
    default Set<String> keySet() {
        return null == source() ? Collections.emptySet() : source().keySet();
    }

    /**
     * 获取前缀
     *
     * @param name 名称
     * @return 前缀
     */
    default String prefix(String name) {
        int index = name.indexOf(SYMBOL_ASTERISK);
        return -1 == index ? name : name.substring(0, index);
    }

    /**
     * 添加数据
     *
     * @param key   索引
     * @param value 值
     * @return this
     */
    default E putIfPresent(String key, Object value) {
        if (null != source() && null != value) {
            source().put(key, value);
        }
        return (E) this;
    }

    /**
     * 添加数据
     *
     * @param key   索引
     * @param value 值
     * @return this
     */
    @Override
    default E put(String key, Object value) {
        if (null != source()) {
            doAnalysis(source(), key, value);
        }
        return (E) this;
    }

    /**
     * 添加数据
     *
     * @param m 值
     */
    @Override
    @SuppressWarnings("ALL")
    default void putAll(Map<? extends String, ? extends Object> m) {
        Map<String, Object> source = source();
        LevelsOpen levelsOpen = new LevelsOpen();
        if(!m.isEmpty()) {
            Map map = new HashMap(m);
            levelsOpen.apply(map).forEach((k, v) -> {
                put(k.toString(), v);
            });
        }

        if (null != source) {
            m.forEach((k, v) -> {
                put(k, v);
            });
        }

    }

    default void doAnalysis(Map<String, Object> source, String k, Object v) {
        if(k == null || v == null) {
            return;
        }
        source.put(k, v);
        source.put(Converter.toCamelHyphen(k), v);
    }


    /**
     * 添加数据
     *
     * @param value 值
     * @return this
     */
    default E putAll(Object value) {
        boolean b = null != source() && (value instanceof Map && !MapUtils.isEmpty((Map) value));
        if (b) {
            putAll(BeanMap.create(value));
        }
        return (E) this;
    }

    /**
     * 删除数据
     *
     * @param key 索引
     * @return this
     */
    @Override
    default E remove(Object key) {
        if (null != source()) {
            source().remove(key);
        }
        return (E) this;
    }

    /**
     * 获取长度
     *
     * @return 长度
     */
    @Override
    default int size() {
        return null == source() ? 0 : source().size();
    }

    /**
     * 元数据
     *
     * @return 元数据
     */
    Map<String, Object> source();

    /**
     * 获取长度
     *
     * @return 长度
     */
    @Override
    default Collection<Object> values() {
        return null == source() ? Collections.emptyList() : source().values();
    }


    //**************************************************************

    /**
     * 保存数据
     *
     * @param map      map
     * @param key      索引
     * @param function 回调
     * @return boolean
     */
    default <K, V, F> boolean operaIfExist(Map<K, V> map, K key, SafeConsumer<V> function) {
        return operaIfExist(map, key, null, function);
    }

    /**
     * 保存数据
     *
     * @param map      map
     * @param key      索引
     * @param type     类型
     * @param function 回调
     * @return b
     */
    default <K, V, F> boolean operaIfExist(Map<K, V> map, K key, Class<F> type, SafeConsumer<F> function) {
        if (null == map || null == key || !map.containsKey(key) || null == function) {
            return false;
        }

        V v = map.get(key);
        if (null == v) {
            return false;
        }
        function.accept(Converter.convertIfNecessary(v, type));
        return true;
    }

    /**
     * Properties
     *
     * @return Properties
     */
    default Properties toProperties() {
        Properties rs = new Properties();
        forEach((k, v) -> {
            if (null == v) {
                return;
            }

            rs.put(k, v);
        });
        return rs;
    }
}