package com.chua.common.support.reflection.reflections.scanners;

import com.chua.common.support.reflection.reflections.vfs.Vfs;
import javassist.bytecode.ClassFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Scanner {@link #scan(ClassFile)} method receives a {@link ClassFile} and produce a list of {@link Map.Entry}.
 * These key/values will be stored under {@link #index()} for querying.
 * <br><br>see more in {@link Scanners}
 *
 * @author Administrator
 */
public interface ResourceScanner {

    /**
     * scan the given {@code classFile} and produces list of {@link Map.Entry} key/values
     *
     * @param classFile class file
     * @return 文件
     */
    List<Map.Entry<String, String>> scan(ClassFile classFile);

    /**
     * scan the given {@code file} and produces list of {@link Map.Entry} key/values
     *
     * @param file file
     * @return 文件
     */
    default List<Map.Entry<String, String>> scan(Vfs.VfsFile file) {
        return null;
    }

    /**
     * unique index name for scanner
     *
     * @return 索引
     */
    default String index() {
        return getClass().getSimpleName();
    }

    /**
     * 输入过滤
     *
     * @param file 文件
     * @return 过滤
     */

    default boolean acceptsInput(String file) {
        return file.endsWith(".class");
    }

    /**
     * 便利
     *
     * @param key   key
     * @param value value
     * @return map
     */
    default Map.Entry<String, String> entry(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * 便利
     *
     * @param keys  key
     * @param value value
     * @return map
     */
    default List<Map.Entry<String, String>> entries(Collection<String> keys, String value) {
        return keys.stream().map(key -> entry(key, value)).collect(Collectors.toList());
    }

    /**
     * 便利
     *
     * @param key   key
     * @param value value
     * @return map
     */
    default List<Map.Entry<String, String>> entries(String key, String value) {
        return Collections.singletonList(entry(key, value));
    }

    /**
     * 便利
     *
     * @param key    key
     * @param values value
     * @return map
     */
    default List<Map.Entry<String, String>> entries(String key, Collection<String> values) {
        return values.stream().map(value -> entry(key, value)).collect(Collectors.toList());
    }
}
