package com.chua.common.support.lang.lambda;


import com.chua.common.support.database.entity.Column;
import com.chua.common.support.database.metadata.DelegateMetadata;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.database.orm.conditions.SFunction;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.MapUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Locale.ENGLISH;

/**
 * Lambda 解析工具类
 *
 * @author HCL, MieMie
 * @since 2018-05-10
 */
public final class LambdaUtils {

    /**
     * 字段映射
     */
    private static final Map<String, Map<String, Column>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> LambdaMeta extract(SFunction<T, ?> func) {
        // 1. IDEA 调试模式下 lambda 表达式是一个代理
        if (func instanceof Proxy) {
            return new IdeaProxyLambdaMeta((Proxy) func);
        }
        // 2. 反射读取
        try {
            Method method = func.getClass().getDeclaredMethod("writeReplace");
            return new ReflectLambdaMeta((java.lang.invoke.SerializedLambda) ClassUtils.setAccessible(method).invoke(func));
        } catch (Throwable e) {
            // 3. 反射失败使用序列化的方式读取
            return new ShadowLambdaMeta(SerializedLambda.extract(func));
        }
    }

    /**
     * 格式化 key 将传入的 key 变更为大写格式
     *
     * <pre>
     *     Assert.assertEquals("USERID", formatKey("userId"))
     * </pre>
     *
     * @param key key
     * @return 大写的 key
     */
    public static String formatKey(String key) {
        return key.toUpperCase(ENGLISH);
    }

    /**
     * 将传入的表信息加入缓存
     *
     * @param metadata 表信息
     */
    public static void installCache(Metadata<?> metadata) {
        COLUMN_CACHE_MAP.put(metadata.getTable(), createColumnCacheMap(metadata));
    }

    /**
     * 缓存实体字段 MAP 信息
     *
     * @param info 表信息
     * @return 缓存 map
     */
    private static Map<String, Column> createColumnCacheMap(Metadata<?> info) {
        Map<String, Column> map;

        if (info.havePK()) {
            map = MapUtils.newHashMapWithExpectedSize(info.getColumn().size() + 1);
            map.put(formatKey(info.getKeyProperty()), info.getKeyColumn());
        } else {
            map = MapUtils.newHashMapWithExpectedSize(info.getColumn().size());
        }

        info.getColumn().forEach(i -> map.put(formatKey(i.getName()), i));
        return map;
    }

    /**
     * 获取实体对应字段 MAP
     *
     * @param clazz 实体类
     * @return 缓存 map
     */
    public static Map<String, Column> getColumnMap(Class<?> clazz) {
        return MapUtils.computeIfAbsent(COLUMN_CACHE_MAP, clazz.getName(), key -> createColumnCacheMap(Metadata.of(clazz)));
    }

}
