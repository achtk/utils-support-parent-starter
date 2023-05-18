package com.chua.common.support.collection;


import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.utils.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单级 Map
 *
 * @author CH
 * @version 1.0.0
 */
public interface FlatMap extends Map<String, Object> {
    /**
     * 添加实体
     *
     * @param entity 实体
     */
    void put(Object entity);

    /**
     * 获取数据
     *
     * @param key 索引
     * @return 数据
     */
    List<Object> wildcard(String key);

    /**
     * 获取数据
     *
     * @param key  索引
     * @param type 类型
     * @return 数据
     */
    default <R> List<R> wildcard(String key, Class<R> type) {
        List<Object> read = wildcard(key);
        List<R> result = new ArrayList<>(read.size());
        for (Object o : read) {
            R forObject = ClassUtils.forObject(type);
            if (null == forObject) {
                break;
            }
            BeanUtils.copyProperties(o, forObject);
            result.add(forObject);
        }
        return result;
    }

    /**
     * 构建集合
     *
     * @return 集合
     */
    static FlatMap create() {
        return new FlatHashMap();
    }

    /**
     * 构建集合
     *
     * @param entity 实体
     * @return 集合
     */
    static FlatMap create(Object entity) {
        return new FlatHashMap(BeanMap.create(entity));
    }

    /**
     * 构建集合
     *
     * @param map 集合
     * @return 集合
     */
    static FlatMap create(Map<String, Object> map) {
        return new FlatHashMap(map);
    }
}
