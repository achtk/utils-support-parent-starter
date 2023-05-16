package com.chua.common.support.file.yaml.document;

import com.chua.common.support.file.yaml.YamlException;

import java.util.Iterator;

/**
 * @author ACHTK
 */
public interface YamlDocument {
    /**
     * 获取标签
     *
     * @return 标签
     */
    String getTag();

    /**
     * 获取长度
     *
     * @return 长度
     */
    int size();

    /**
     * 获取实体
     *
     * @param key k
     * @return 实体
     * @throws YamlException ex
     */
    YamlEntry getEntry(String key) throws YamlException;

    /**
     * 获取实体
     *
     * @param index index
     * @return 实体
     * @throws YamlException ex
     */
    YamlEntry getEntry(int index) throws YamlException;

    /**
     * 删除实体
     *
     * @param key k
     * @return 实体
     * @throws YamlException ex
     */
    boolean deleteEntry(String key) throws YamlException;

    /**
     * 设置实体
     *
     * @param key   k
     * @param value v
     * @throws YamlException ex
     */
    void setEntry(String key, boolean value) throws YamlException;

    /**
     * 设置实体
     *
     * @param key   k
     * @param value v
     * @throws YamlException ex
     */
    void setEntry(String key, Number value) throws YamlException;

    /**
     * 设置实体
     *
     * @param key   k
     * @param value v
     * @throws YamlException ex
     */
    void setEntry(String key, String value) throws YamlException;

    /**
     * 设置实体
     *
     * @param key   k
     * @param value v
     * @throws YamlException ex
     */
    void setEntry(String key, AbstractYamlElement value) throws YamlException;

    /**
     * 获取节点
     *
     * @param item element
     * @return AbstractYamlElement
     * @throws YamlException ex
     */
    AbstractYamlElement getElement(int item) throws YamlException;

    /**
     * 删除节点
     *
     * @param element element
     * @throws YamlException ex
     */
    void deleteElement(int element) throws YamlException;

    /**
     * 设置节点
     *
     * @param item  item
     * @param value value
     * @throws YamlException ex
     */
    void setElement(int item, boolean value) throws YamlException;

    /**
     * 设置节点
     *
     * @param item  item
     * @param value value
     * @throws YamlException ex
     */
    void setElement(int item, Number value) throws YamlException;

    /**
     * 设置节点
     *
     * @param item  item
     * @param value value
     * @throws YamlException ex
     */
    void setElement(int item, String value) throws YamlException;

    /**
     * 设置节点
     *
     * @param item    item
     * @param element element
     * @throws YamlException ex
     */
    void setElement(int item, AbstractYamlElement element) throws YamlException;

    /**
     * 设置节点
     *
     * @param value value
     * @throws YamlException ex
     */
    void addElement(boolean value) throws YamlException;

    /**
     * 设置节点
     *
     * @param value value
     * @throws YamlException ex
     */
    void addElement(Number value) throws YamlException;

    /**
     * 设置节点
     *
     * @param value value
     * @throws YamlException ex
     */
    void addElement(String value) throws YamlException;

    /**
     * 设置节点
     *
     * @param element element
     * @throws YamlException ex
     */
    void addElement(AbstractYamlElement element) throws YamlException;

    /**
     * 迭代器
     *
     * @return Iterator
     * @throws YamlException ex
     */
    Iterator iterator();

}
