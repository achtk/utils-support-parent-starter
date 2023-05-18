package com.chua.common.support.utils;

import com.chua.common.support.collection.MultiLinkedValueMap;
import com.chua.common.support.collection.MultiValueMap;
import com.chua.common.support.constant.NumberConstant;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.Splitter;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.EMPTY_STRING_ARRAY;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;

/**
 * Map工具类
 *
 * @author CH
 */
public class MapUtils {

    /**
     * 合并姐
     *
     * @param source 集合
     * @return 合并的集合
     */
    public static <K, V> Map<K, V> merge(final Map<K, V>... source) {
        if (null == source) {
            return Collections.emptyMap();
        }
        Map<K, V> result = new HashMap<>(source.length);
        for (Map<K, V> map : source) {
            if (isEmpty(map)) {
                continue;
            }
            result.putAll(map);
        }
        return result;
    }

    /**
     * 以null/""安全的方式从Map获取字符串。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @return Map中的值作为字符串，如果为null，则<code> defaultValue </ code>
     */
    public static <K, V> String getStringForEmpty(final Map<K, V> map, final K key, final K key2, final String defaultValue) {
        if (null == map) {
            return defaultValue;
        }

        Object v = getObject(map, key);
        return null == v || "".equals(v) ? (v = getObject(map, key2)) == null ? defaultValue : v.toString() : v.toString();
    }


    /**
     * 获取值当值不存在赋值并取出
     *
     * @param map   集合
     * @param key   索引
     * @param value 值
     * @return 值
     */
    public static <K, V> V getComputeIfAbsent(Map<K, V> map, K key, V value) {
        if (map == null || null == value) {
            return null;
        }
        V v = map.get(key);
        if (null == v) {
            v = map.put(key, value);
            if (null == v) {
                v = value;
            }
        }
        return v;
    }

    /**
     * 空安全检查指定的Map是否为空。 * * <p> Null返回true。
     *
     * @param properties 要检查的集合，可以为null
     * @return 如果为空或null，则为true
     */
    public static boolean isEmpty(Properties properties) {
        return (properties == null || properties.isEmpty());
    }

    /**
     * 空安全检查指定的Dictionary是否为空。 * * <p> Null返回true。
     *
     * @param map 要检查的集合，可以为null
     * @return 如果为空或null，则为true
     */
    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 空安全检查指定的Dictionary是否为空。 * * <p> Null返回true。
     *
     * @param dictionary 要检查的集合，可以为null
     * @return 如果为空或null，则为true
     */
    public static boolean isEmpty(Dictionary<?, ?> dictionary) {
        return (dictionary == null || dictionary.isEmpty());
    }

    /**
     * 在给定映射中查找给定键，如果转换失败，则使用默认值将结果转换为。
     *
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @param map          集合
     * @param key          在Map中查找的值的关键
     * @param defaultValue 如果值为null或转换失败，将返回defaultValue
     * @return 映射中的值为数字的值；如果
     * 原始值为null，映射为null或数字转换
     * 失败，则为defaultValue
     */
    public static <K, V> Number getNumber(final Map<K, V> map, K key, Number defaultValue) {
        Number answer = getNumber(map, key);
        return null == answer ? defaultValue : answer;
    }

    /**
     * 以null安全的方式从Map获取数字。
     * <p> 如果该值为<code> Number </ code>，则直接返回。
     * 如果值是<code> String </ code>，则使用系统默认格式程序上的
     * {@link NumberFormat ＃parse（String）}进行转换
     * 如果转换失败，则返回<code> null </ code>。
     * 否则，返回<code> null </ code>。
     *
     * @param <K> 索引类型
     * @param <V> 数据类型
     * @param map 集合
     * @param key 查找的关键
     * @return Map中的值作为Number，如果Map输入为空，则为<code> null </ code>
     */
    public static <K, V> Number getNumber(final Map<K, V> map, final K key) {
        Object answer = getObject(map, key);
        if (answer != null) {
            if (answer instanceof Number) {
                return (Number) answer;
            } else if (answer instanceof String) {
                try {
                    String text = (String) answer;
                    return NumberFormat.getInstance().parse(text);
                } catch (ParseException e) {
                    // 失败意味着返回null
                }
            }
        }
        return null;
    }

    /**
     * 获取 Object
     *
     * @param map 集合
     * @param key 索引
     * @return Map中的值，如果Map输入为空，则为<code> null </ code>
     */
    public static <K, V> V getObject(final Map<K, V> map, final K key) {
        return map != null ? map.get(key) : null;
    }

    /**
     * 获取 Object
     *
     * @param map  集合
     * @param keys 索引
     * @return Map中的值，如果Map输入为空，则为<code> null </ code>
     */
    public static <K, V> Object getObject(final Map<K, V> map, final K... keys) {
        for (K key : keys) {
            V v = map.get(key);
            if (null != v) {
                return v;
            }
        }
        return null;
    }

    /**
     * 获取 Object
     *
     * @param map          集合
     * @param key          索引
     * @param defaultValue 默认值
     * @return Map中的值，如果Map输入为空，则为<code> null </ code>
     */
    public static <K, V> Object getObject(final Map<K, V> map, final K key, final Object defaultValue) {
        Object object = getObject(map, key);
        if (object != null) {
            return object;
        }
        return defaultValue;
    }

    /**
     * 获取 Object
     *
     * @param map          集合
     * @param key          索引
     * @param defaultValue 默认值
     * @param type         类型
     * @return Map中的值，如果Map输入为空，则为<code> null </ code>
     */
    public static <K, V, R> R getType(final Map<K, V> map, final K key, final R defaultValue, final Class<R> type) {
        Object object = getObject(map, key);
        return null != object && type.isAssignableFrom(object.getClass()) ? (R) object : defaultValue;
    }

    /**
     * 获取 Object
     *
     * @param map  集合
     * @param key  索引
     * @param type 类型
     * @return Map中的值，如果Map输入为空，则为<code> null </ code>
     */
    public static <K, V, R> R getType(final Map<K, V> map, final K key, final Class<R> type) {
        Object object = getObject(map, key);
        return null != object && type.isAssignableFrom(object.getClass()) ? (R) object : null;
    }

    /**
     * 以null安全的方式从Map获取字符串。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return Map中的值作为字符串，如果为null，则<code> null </ code>
     */
    public static <K, V> String getString(final Map<K, V> map, final K key) {
        Object object = getObject(map, key);
        if (null == object) {
            return null;
        }
        return object.toString();
    }

    /**
     * 以null安全的方式从Map获取字符串。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @return Map中的值作为字符串，如果为null，则<code> defaultValue </ code>
     */
    public static <K, V> String getString(final Map<K, V> map, final List<K> key, final String defaultValue) {
        for (K k : key) {
            Object object = getObject(map, k);
            if (null != object) {
                return object.toString();
            }
        }
        return defaultValue;
    }

    /**
     * 以null安全的方式从Map获取字符串。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map  集合
     * @param keys 查找的关键
     * @param <K>  索引类型
     * @param <V>  数据类型
     * @return Map中的值作为字符串，如果为null，则<code> defaultValue </ code>
     */
    public static <K, V> String getString(final Map<K, V> map, final K... keys) {
        for (K k : keys) {
            Object object = getObject(map, k);
            if (null != object) {
                return object.toString();
            }
        }
        return null;
    }

    /**
     * 以null安全的方式从Map获取字符串。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @return Map中的值作为字符串，如果为null，则<code> defaultValue </ code>
     */
    public static <K, V> String getString(final Map<K, V> map, final K key, final K key2, final String defaultValue) {
        if (null == map) {
            return defaultValue;
        }

        Object v = getObject(map, key);
        return null == v ? (v = getObject(map, key2)) == null ? defaultValue : v.toString() : v.toString();
    }

    /**
     * 在给定映射中查找给定键，如果转换失败，则使用默认值将结果转换为字符串。
     *
     * @param map          集合
     * @param key          在该Map中查找的值的关键
     * @param defaultValue 如果该值为null或转换失败，将返回{defaultValue}
     * @return 映射中的值作为字符串；如果原始值为null，映射为null或字符串转换，则为defaultValue
     */
    public static <K, V> String getString(final Map<K, V> map, final K key, final String defaultValue) {
        String answer = getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    /**
     * 以null安全的方式从Map获取字符串数组。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map       集合
     * @param key       查找的关键
     * @param delimiter 分隔符
     * @return Map中的值作为字符串，如果为null，则<code> [] </ code>
     */
    public static <K, V> String[] getStringArray(final Map<K, V> map, final K key, final String delimiter) {
        Object object = getObject(map, key);
        if (null == object) {
            return EMPTY_STRING_ARRAY;
        }
        if (object instanceof String[]) {
            return Arrays.stream((String[]) object).filter(Objects::nonNull).toArray(String[]::new);
        }

        String string = object.toString();
        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(string).toArray(new String[0]);
    }

    /**
     * 以null安全的方式从Map获取字符串数组。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return Map中的值作为字符串，如果为null，则<code> [] </ code>
     */
    public static <K, V> String[] getStringArray(final Map<K, V> map, final K key, final String[] defaultValue) {
        Object object = getObject(map, key);
        if (null == object) {
            return defaultValue;
        }
        if (object instanceof String[]) {
            return (String[]) object;
        }

        String string = object.toString();
        return string.split(",");
    }

    /**
     * 以null安全的方式从Map获取字符串数组。默认以, 分隔
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return Map中的值作为字符串，如果为null，则<code> [] </ code>
     */
    public static <K, V> String[] getStringArray(final Map<K, V> map, final K key) {
        return getStringArray(map, key, SYMBOL_COMMA);
    }

    /**
     * 以null安全的方式从Map中获取Date，。
     * <p> Date是从 的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 如果输入的Map为空，则将Map中的值返回为Date，<code> null </ code>
     */
    public static <K, V> Date getDate(final Map<K, V> map, final K key) {
        Object answer = getObject(map, key);
        if (null == answer) {
            return null;
        }
        if (answer instanceof Date) {
            return (Date) answer;
        }
        if (answer instanceof Long) {
            return new Date((Long) answer);
        }

        if (answer instanceof String) {
            DateFormat dateFormat = new SimpleDateFormat();
            try {
                return dateFormat.parse((String) answer);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 以null安全的方式从Map获取Double。
     * <p> 从{@link #getNumber（Object）}的结果中获得Double。
     *
     * @param <K> 索引类型
     * @param <V> 数据类型
     * @param map 集合
     * @param key 查找的关键
     * @return 如果输入的Map为空，则Map中的值为Double，<code> null </ code>
     */
    public static <K, V> Double getDouble(final Map<K, V> map, final K key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Double) {
            return (Double) answer;
        }
        return answer.doubleValue();
    }

    /**
     * 以null安全的方式从Map获取Double。
     * <p> 从{@link #getNumber（Object）}的结果中获得Double。
     *
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 如果输入的Map为空，则Map中的值为Double，<code> null </ code>
     */
    public static <K, V> Double getDouble(final Map<K, V> map, final K key, final Double defaultValue) {
        Double aDouble = getDouble(map, key);
        return null == aDouble ? defaultValue : aDouble;
    }

    /**
     * 以null安全的方式从Map获取Double。
     * <p> 从{@link #getDouble(Map, Object)}的结果中获得Double。
     *
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 如果输入的Map为空，则Map中的值为Double，<code> defaultValue </ code>
     */
    public static <K, V> Double getDouble(final Map<K, V> map, final K key, final K key2, final Double defaultValue) {
        if (null == map) {
            return defaultValue;
        }
        Double aDouble = getDouble(map, key);
        return null == aDouble ? (aDouble = getDouble(map, key2)) == null ? defaultValue : aDouble : aDouble;
    }

    /**
     * 以null安全的方式从Map获取Double。
     * <p> 从{@link #getNumber（Object）}的结果中获得Double。
     *
     * @param <K> 索引类型
     * @param <V> 数据类型
     * @param map 集合
     * @param key 查找的关键
     * @return 如果输入的Map为空，则Map中的值为Double，<code> 0 </ code>
     */
    public static <K, V> double getDoubleValue(final Map<K, V> map, final K key) {
        Double aDouble = getDouble(map, key);
        return null == aDouble ? 0D : aDouble;
    }

    /**
     * 以null安全的方式从Map获取Double。
     * <p> 从{@link #getNumber（Object）}的结果中获得Double。
     *
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 如果输入的Map为空，则Map中的值为Double，<code> 0 </ code>
     */
    public static <K, V> double getDoubleValue(final Map<K, V> map, final K key, final double defaultValue) {
        Double aDouble = getDouble(map, key);
        return null == aDouble ? defaultValue : aDouble;
    }


    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> 0 </ code>
     */
    public static <K, V> int getIntValue(final Map<K, V> map, final K key) {
        Integer integer = getInteger(map, key);
        return null == integer ? 0 : integer;
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> 0 </ code>
     */
    public static <K, V> int getIntValue(final Map<K, V> map, final K key, final int defaultValue) {
        Integer integer = getInteger(map, key);
        return null == integer ? defaultValue : integer;
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Integer getInteger(final Map<K, V> map, final K key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return answer.intValue();
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Integer getInteger(final Map<K, V> map, final K key, final Integer defaultValue) {
        Integer integer = getInteger(map, key);
        return null == integer ? defaultValue : integer;
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> 0 </ code>
     */
    public static <K, V> long getLongValue(final Map<K, V> map, final K key) {
        Long value = getLong(map, key);
        return null == value ? 0 : value;
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> 0 </ code>
     */
    public static <K, V> long getLongValue(final Map<K, V> map, final K key, final long defaultValue) {
        Long aLong = getLong(map, key);
        return null == aLong ? defaultValue : aLong;
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Long getLong(final Map<K, V> map, final K key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return answer.longValue();
    }

    /**
     * 以null安全的方式从Map获取一个整数。
     * <p> 整数是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 以整数形式返回Map中的值，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Long getLong(final Map<K, V> map, final K key, final Long defaultValue) {
        Long aLong = getLong(map, key);
        return null == aLong ? defaultValue : aLong;
    }

    /**
     * 获取Boolean
     *
     * @param map 集合
     * @param key 查找的关键
     * @return Boolean
     */
    public static <K, V> Boolean getBoolean(final Map<K, V> map, final K key) {
        Object answer = getObject(map, key);
        if (answer != null) {
            if (answer instanceof Boolean) {
                return (Boolean) answer;

            } else if (answer instanceof String) {
                return Boolean.valueOf((String) answer);

            } else if (answer instanceof Number) {
                Number n = (Number) answer;
                return (n.intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        return null;
    }

    /**
     * 获取Boolean
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return Boolean
     */
    public static <K, V> Boolean getBoolean(final Map<K, V> map, final K key, final Boolean defaultValue) {
        Boolean aBoolean = getBoolean(map, key);
        return null == aBoolean ? defaultValue : aBoolean;
    }

    /**
     * 以null安全的方式从Map获取一个字节。
     * <p> 字节是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 将Map中的值作为字节返回，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Byte getByte(final Map<K, V> map, final K key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Byte) {
            return (Byte) answer;
        }
        return answer.byteValue();
    }

    /**
     * 以null安全的方式从Map获取一个字节。
     * <p> 字节是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 将Map中的值作为字节返回，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Byte getByte(final Map<K, V> map, final K key, final Byte defaultValue) {
        Byte aByte = getByte(map, key);
        return null == aByte ? defaultValue : aByte;
    }

    /**
     * 以null安全的方式从Map获取一个字节。
     * <p> 字节是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 将Map中的值作为字节返回，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> byte getByteValue(final Map<K, V> map, final K key) {
        Byte aByte = getByte(map, key);
        return null == aByte ? (byte) 0 : aByte;
    }

    /**
     * 以null安全的方式从Map获取一个字节。
     * <p> 字节是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 将Map中的值作为字节返回，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> byte getByteValue(final Map<K, V> map, final K key, final byte defaultValue) {
        Byte aByte = getByte(map, key);
        return null == aByte ? defaultValue : aByte;
    }

    /**
     * 以null安全的方式从Map获取Float。
     * <p> 浮点数是根据{@link #getNumber（Object）}的结果获得的。
     *
     * @param <K> 索引类型
     * @param <V> 数据类型
     * @param map 集合
     * @param key 查找的关键
     * @return 以浮点数形式返回Map中的值，如果输入的Map为空，则为<code> null </ code>
     */
    public static <K, V> Float getFloat(final Map<K, V> map, final K key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Float) {
            return (Float) answer;
        }
        return answer.floatValue();
    }

    /**
     * 以null安全的方式从Map获取Float。
     * <p> 浮点数是根据{@link #getNumber（Object）}的结果获得的。
     *
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 以浮点数形式返回Map中的值，如果输入的Map为空，则为<code> defaultValue </ code>
     */
    public static <K, V> Float getFloat(final Map<K, V> map, final K key, final Float defaultValue) {
        Float aFloat = getFloat(map, key);
        return null == aFloat ? defaultValue : aFloat;
    }

    /**
     * 以null安全的方式从Map获取Float。
     * <p> 浮点数是根据{@link #getNumber（Object）}的结果获得的。
     *
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @param map          集合
     * @param key          查找的关键
     * @param key2         查找的关键
     * @param defaultValue 默认值
     * @return 以浮点数形式返回Map中的值，如果输入的Map为空，则为<code> defaultValue </ code>
     */
    public static <K, V> Float getFloat(final Map<K, V> map, final K key, final K key2, final Float defaultValue) {
        Float aFloat = getFloat(map, key);
        return null == aFloat ? (aFloat = getFloat(map, key2)) == null ? defaultValue : aFloat : aFloat;
    }

    /**
     * 以null安全的方式从Map获取Float。
     * <p> 浮点数是根据{@link #getNumber（Object）}的结果获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @param <K> 索引类型
     * @param <V> 数据类型
     * @return 以浮点数形式返回Map中的值，如果输入的Map为空，则为<code> 0 </ code>
     */
    public static <K, V> float getFloatValue(final Map<K, V> map, final K key) {
        Float aFloat = getFloat(map, key);
        return null == aFloat ? 0f : aFloat;
    }

    /**
     * 以null安全的方式从Map获取Float。
     * <p> 浮点数是根据{@link #getNumber（Object）}的结果获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @param <K>          索引类型
     * @param <V>          数据类型
     * @return 以浮点数形式返回Map中的值，如果输入的Map为空，则为<code> 0 </ code>
     */
    public static <K, V> float getFloatValue(final Map<K, V> map, final K key, final float defaultValue) {
        Float aFloat = getFloat(map, key);
        return null == aFloat ? defaultValue : aFloat;
    }

    /**
     * 以null安全的方式从Map中获取一个Short。
     * <p> Short是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 如果输入的Map为空，则以Short<code> null </ code>返回Map中的值
     */
    public static <K, V> Short getShort(final Map<K, V> map, final K key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Short) {
            return (Short) answer;
        }
        return answer.shortValue();
    }

    /**
     * 以null安全的方式从Map中获取一个Short。
     * <p> Short是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 如果输入的Map为空，则以Short<code> null </ code>返回Map中的值
     */
    public static <K, V> Short getShort(final Map<K, V> map, final K key, final Short defaultValue) {
        Short aShort = getShort(map, key);
        return null == aShort ? defaultValue : aShort;
    }

    /**
     * 以null安全的方式从Map中获取一个Short。
     * <p> Short是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return 如果输入的Map为空，则以Short<code> 0 </ code>返回Map中的值
     */
    public static <K, V> short getShortValue(final Map<K, V> map, final K key) {
        Short aShort = getShort(map, key);
        return null == aShort ? 0 : aShort;
    }

    /**
     * 以null安全的方式从Map中获取一个Short。
     * <p> Short是从{@link #getNumber（Object）}的结果中获得的。
     *
     * @param map          集合
     * @param key          查找的关键
     * @param defaultValue 默认值
     * @return 如果输入的Map为空，则以Short<code> 0 </ code>返回Map中的值
     */
    public static <K, V> short getShortValue(final Map<K, V> map, final K key, final short defaultValue) {
        Short aShort = getShort(map, key);
        return null == aShort ? defaultValue : aShort;
    }


    /**
     * 以null安全的方式从Map获取字符串。
     * <p>字符串是通过<code> toString </ code>获得的。
     *
     * @param map 集合
     * @param key 查找的关键
     * @return Map中的值作为字符串，如果为null，则<code> null </ code>
     */
    public static <K, V> File getFile(final Map<K, V> map, final K key) {
        Object object = getObject(map, key);
        if (null == object) {
            return null;
        }
        if (object instanceof File) {
            return (File) object;
        }
        return Converter.convertIfNecessary(object, File.class);
    }

    /**
     * 获取值当值不存在赋值并取出
     *
     * @param map      集合
     * @param key      索引
     * @param function 回调
     * @return 值
     */
    public static <K, V> V getComputeIfFunction(Map<K, V> map, K key, Function<K, V> function) {
        if (map == null) {
            return null;
        }
        V v = map.get(key);
        if (null == v && null != function) {
            v = map.put(key, function.apply(key));
        }
        v = map.get(key);
        return v;
    }

    /**
     * 初始化
     *
     * @param value             数据
     * @param valueSeparator    数据分隔符
     * @param keyValueSeparator 索引分隔符
     * @return this
     */
    public static Map<String, String> asMap(String value, char valueSeparator, char keyValueSeparator) {
        return asMap(value, String.valueOf(valueSeparator), String.valueOf(keyValueSeparator));
    }

    /**
     * 初始化
     *
     * @param value             数据
     * @param valueSeparator    数据分隔符
     * @param keyValueSeparator 索引分隔符
     * @return this
     */
    public static Map<String, String> asMap(String value, String valueSeparator, String keyValueSeparator) {
        if (StringUtils.isEmpty(value)) {
            return Collections.emptyMap();
        }

        Map<String, Object> source = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        String[] split;
        if (StringUtils.isNotEmpty(valueSeparator)) {
            split = value.split(valueSeparator);
        } else {
            split = new String[]{value};
        }

        if (split.length != 0) {
            for (String item : split) {
                String[] strings = item.split(keyValueSeparator, 2);
                if (strings.length == 0) {
                    continue;
                }
                String mapKey;
                String mapValue = null;
                if (strings.length == 1) {
                    mapKey = strings[0].trim();
                } else {
                    mapKey = strings[0].trim();
                    mapValue = strings[1].trim();
                }

                try {
                    convertToList(source, mapKey, null == mapValue ? null : URLDecoder.decode(mapValue, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
            }
        }
        Map<String, String> result = new HashMap<>(source.size());
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value1 = entry.getValue();
            result.put(entry.getKey(), value1 instanceof Collection && ((Collection<?>) value1).size() == 1 ? CollectionUtils.findFirst((Collection) value1).toString() : value1.toString());
        }
        return Collections.unmodifiableMap(result);
    }

    /**
     * 数据合并
     *
     * @param target 集合
     * @param key    索引
     * @param value  值
     */
    @SuppressWarnings("ALL")
    private static void convertToList(Map<String, Object> target, String key, Object value) {
        Object computeIfAbsent = target.computeIfAbsent(key, (Function<String, List<Object>>) input -> new ArrayList());
        if (null == value) {
            return;
        }

        if (computeIfAbsent instanceof List) {
            ((List) computeIfAbsent).add(value);
        } else {
            List<Object> newValue = new ArrayList();
            newValue.add(computeIfAbsent);
            newValue.add(value);
            target.put(key, newValue);
        }
    }


    /**
     * 获取第一个数据
     *
     * @param kvMap 集合
     * @param <K>   key类型
     * @param <V>   value类型
     * @return 集合第一个数据, 集合为空或者无数据返回null
     */
    public static <K, V> Map.Entry<K, V> getFirst(final Map<K, V> kvMap) {
        if (isEmpty(kvMap)) {
            return null;
        }
        return kvMap.entrySet().iterator().next();
    }


    /**
     * properties
     *
     * @param map map
     * @return prop
     */
    public static Properties asProp(Map<?, ?> map) {
        Properties properties = new Properties();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (null == entry.getValue()) {
                continue;
            }

            properties.put(entry.getKey(), entry.getValue());
        }

        return properties;
    }

    /**
     * prop -> map
     *
     * @param properties prop
     * @return map
     */
    public static Map<String, Object> asMap(Properties properties) {
        Map<String, Object> rs = new HashMap<>(properties.size());
        properties.forEach((k, v) -> {
            rs.put(k.toString(), v);
        });
        return rs;
    }

    public static <K, V> Map<String, V> appendPre(String pre, Map<K, V> kvMap) {
        Map<String, V> tmp = new HashMap<>(kvMap.size());
        for (Map.Entry<K, V> entry : kvMap.entrySet()) {
            tmp.put(pre + entry.getKey().toString(), entry.getValue());
        }

        return tmp;
    }

    /**
     * 默认值判断
     *
     * @param source       原始数据
     * @param defaultValue 默认值
     * @param <K>          类型
     * @param <V>          类型
     * @return 结果
     */
    public static <K, V> Map<K, V> defaultValue(Map<K, V> source, Map<K, V> defaultValue) {
        return isEmpty(source) ? defaultValue : source;
    }

    /**
     * 补充默认数据到原始集合
     *
     * @param source       原始数据
     * @param defaultValue 默认值
     * @param <K>          类型
     * @param <V>          类型
     */
    public static <K, V> Map<K, V> compute(Map<K, V> source, Map<K, V> defaultValue) {
        if (isEmpty(source)) {
            return defaultValue;
        }

        Map<K, V> temp = new LinkedHashMap<>(source);
        for (Map.Entry<K, V> entry : defaultValue.entrySet()) {
            if (temp.containsKey(entry.getKey())) {
                continue;
            }

            temp.put(entry.getKey(), entry.getValue());
        }

        return temp;
    }

    /**
     * 过滤空置
     *
     * @param map      集合
     * @param name     索引
     * @param consumer 回调
     * @param <V>      类型
     */
    @SuppressWarnings("unchecked")
    public static <V> void filterNone(Map<String, V> map, Consumer<V> consumer, String... name) {
        for (String s : name) {
            Object object = getObject(map, s);
            if (null == object) {
                continue;
            }

            consumer.accept((V) object);
        }
    }

    /**
     * 集合转化
     *
     * @param source 来源
     * @return 目标
     */
    @SuppressWarnings("ALL")
    public static Map<String, String> asStringMap(Map source) {
        Map<String, String> tpl = new LinkedHashMap<>(source.size());
        source.forEach((k, v) -> {
            tpl.put(k.toString(), v.toString());
        });
        return tpl;
    }

    /**
     * 集合转化
     *
     * @param source 来源
     * @return 目标
     */
    public static Map<String, String> asStringMap(Properties source) {
        Map<String, String> tpl = new LinkedHashMap<>(source.size());
        for (Map.Entry<Object, Object> entry : source.entrySet()) {
            tpl.put(entry.getKey().toString(), null == entry.getValue() ? null : entry.getValue().toString());
        }
        return tpl;
    }

    /**
     * 生成索引集合
     *
     * @param valuesInOrder list
     * @param <T>           类型
     * @return 索引集合
     */
    public static <T> Map<T, Integer> indexMap(List<T> valuesInOrder) {
        Map<T, Integer> rs = new HashMap<>(valuesInOrder.size());
        int i = 0;
        for (T t : valuesInOrder) {
            rs.put(t, i++);
        }

        return rs;
    }

    /**
     * 获取key
     * @return key
     */
    @SuppressWarnings("unchecked")
    public static <K extends Object> Function<Map.Entry<K, ?>, K> keyFunction() {
        return Map.Entry::getKey;
    }

    /**
     * 获取value
     * @return value
     */
    @SuppressWarnings("unchecked")
    public static <V extends Object> Function<Map.Entry<?, V>, V> valueFunction() {
        return Map.Entry::getValue;
    }


    /**
     * 初始化
     * @return map
     * @param <K> k
     * @param <V> v
     */
    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<>(DEFAULT_INITIAL_CAPACITY);
    }
    /**
     * HashMap
     * @param k k
     * @param v v
     * @return map
     */
    public static <K, V>Map<K, V> ofHashMap(K k, V v) {
        Map<K, V> rs = new HashMap<>();
        rs.put(k, v);
        return rs;
    }
    /**
     * multimap
     * @param k k
     * @param v v
     * @return map
     */
    public static <K, V>Map<K, V> ofLinkedMap(K k, V v) {
        Map<K, V> rs = new LinkedHashMap<>();
        rs.put(k, v);
        return rs;
    }
    /**
     * multimap
     * @param k k
     * @param v v
     * @return map
     */
    public static <K, V>MultiValueMap<K, V> ofMultiMap(K k, V v) {
        MultiValueMap<K, V> rs = new MultiLinkedValueMap<>();
        rs.add(k, v);
        return rs;
    }
    /**
     * multimap
     * @return map
     */
    public static <K, V>MultiValueMap<K, V> ofMultiMap() {
        return new MultiLinkedValueMap<>();
    }
}
