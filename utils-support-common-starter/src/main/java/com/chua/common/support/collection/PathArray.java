package com.chua.common.support.collection;

import java.util.*;

/**
 * path map
 *
 * @author CH
 */
public interface PathArray extends List<Object> {
    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default PathArray getArray(int key) {
        Object o = get(key);

        if (null == o) {
            return new PathLinkedArray(new LinkedList<>());
        }

        if (o instanceof PathArray) {
            return (PathArray) o;
        }

        if (o instanceof Collection) {
            return new PathLinkedArray(new LinkedList<>((Collection<?>) o));
        }

        throw new IllegalStateException("类型不匹配");
    }

    /**
     * 获取数据
     *
     * @param key key
     * @return 结果
     */
    default PathMap getPath(int key) {
        Object o = get(key);


        if (null == o) {
            return new PathLinkedMap(new LinkedHashMap<>());
        }

        if (o instanceof PathMap) {
            return (PathMap) o;
        }

        if (o instanceof Map) {
            return new PathLinkedMap((Map) o);
        }

        throw new IllegalStateException("类型不匹配");
    }

}
