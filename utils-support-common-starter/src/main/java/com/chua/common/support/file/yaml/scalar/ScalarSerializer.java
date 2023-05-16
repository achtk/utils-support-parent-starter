package com.chua.common.support.file.yaml.scalar;

import com.chua.common.support.file.yaml.YamlException;

/**
 * @author Nathan Sweet
 */
public interface ScalarSerializer<T> {
    /**
     * 写入
     * @param object object
     * @return str
     * @throws YamlException ex
     */
    abstract public String write(T object) throws YamlException;

    /**
     * 读取
     * @param value v
     * @return T
     * @throws YamlException ex
     */
    abstract public T read(String value) throws YamlException;
}
