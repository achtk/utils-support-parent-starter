package com.chua.common.support.table;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.lang.profile.Profile;
import com.chua.common.support.lang.profile.ProfileBuilder;
import lombok.experimental.Accessors;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 表
 *
 * @author CH
 */
@Accessors(fluent = true)
public class ConnectorMetadata implements InitializingAware {

    public static final String DIRECTORY = "directory";
    final Profile attributes = ProfileBuilder.newBuilder().build();

    public ConnectorMetadata(String type) {
        attributes.addProfile("type", type);
    }
    /**
     * host
     *
     * @param host 名称
     * @return 名称
     */
    public ConnectorMetadata host(String host) {
        attributes.addProfile("host", host);
        return this;
    }
    /**
     * port
     *
     * @param port 名称
     * @return 名称
     */
    public ConnectorMetadata port(String port) {
        attributes.addProfile("port", port);
        return this;
    }
    /**
     * 文件
     *
     * @param type 名称
     * @return 名称
     */
    public ConnectorMetadata javaType(Class<?> type) {
        attributes.addProfile("javaType", type);
        return this;
    }
    /**
     * 文件
     *
     * @param file 名称
     * @return 名称
     */
    public ConnectorMetadata file(String file) {
        attributes.addProfile("file", file);
        attributes.addProfile(DIRECTORY, file);
        return this;
    }
    /**
     * 名称
     *
     * @param name 名称
     * @return 名称
     */
    public ConnectorMetadata name(String name) {
        attributes.addProfile("name", name);
        return this;
    }

    /**
     * 字段
     *
     * @param columnName 名称
     * @param columnType 映射
     * @return 名称
     */
    public ConnectorMetadata addColumn(String columnName, String columnType) {
        attributes.addMapProfile("column", columnName, columnType);
        return this;
    }

    /**
     * 参数
     *
     * @param data 数据
     * @return 名称
     */
    public ConnectorMetadata addObjectData(Object data) {
        attributes.addListProfile("data", data);
        return this;
    }

    /**
     * 参数
     *
     * @param data 数据
     * @return 名称
     */
    public ConnectorMetadata addObjectData(List<Object> data) {
        attributes.addListProfile("data", data);
        return this;
    }

    /**
     * 参数
     *
     * @param data 数据
     * @return 名称
     */
    public ConnectorMetadata addData(Map<String, Object> data) {
        attributes.addListProfile("data", data);
        return this;
    }

    /**
     * 参数
     *
     * @param data 数据
     * @return 名称
     */
    public ConnectorMetadata addData(List<Map<String, Object>> data) {
        attributes.addListProfile("data", data);
        return this;
    }

    /**
     * 参数
     *
     * @param name    名称
     * @param mapping 映射
     * @return 名称
     */
    public ConnectorMetadata addMapping(String name, String mapping) {
        attributes.addMapProfile("mapping", name, mapping);
        return this;
    }

    /**
     * 参数
     *
     * @param mapping 映射
     * @return 名称
     */
    public ConnectorMetadata addMapping(Map<String, String> mapping) {
        mapping.forEach((k, v) -> {
            attributes.addMapProfile("mapping", k, v);
        });
        return this;
    }


    /**
     * strategy参数
     *
     * @param strategy strategy字段
     * @return 名称
     */
    public ConnectorMetadata strategy(String strategy) {
        attributes.addProfile("strategy", strategy);
        return this;
    }

    /**
     * 参数
     *
     * @param name  名称
     * @param value 值
     * @return 名称
     */
    public ConnectorMetadata addParam(String name, Object value) {
        attributes.addProfile(name, value);
        return this;
    }

    /**
     * 模式
     *
     * @param value 名称
     * @return 名称
     */
    public ConnectorMetadata mode(String value) {
        attributes.addProfile("mode", value);
        return this;
    }

    /**
     * 参数
     *
     * @param value 值
     * @return 名称
     */
    public ConnectorMetadata dataSource(DataSource value) {
        addParam("datasource", value);
        return addParam("dataSource", value);
    }

    public static ConnectorMetadata create(String type) {
        return new ConnectorMetadata(type);
    }


    @Override
    public void afterPropertiesSet() {
    }

    /**
     * 獲取數據
     *
     * @return 结果
     */
    public Profile getConfig() {
        return attributes;
    }

    /**
     * 獲取數據
     *
     * @param name 字段
     * @param type 类型
     * @param <T>  类型
     * @return 结果
     */
    public <T> T get(String name, Class<T> type) {
        return Converter.convertIfNecessary(attributes.getObject(name), type);
    }

    /**
     * 獲取數據
     * @param name 字段
     * @param type 类型
     * @return 结果
     * @param <T> 类型
     */
    public <T>T get(String name, T defaultValue, Class<T> type) {
        T type1 = get(name, type);
        return null == type1 ? defaultValue : type1;
    }

    /**
     * 是否存在
     * @param name 名称
     * @return 是否存在
     */
    public boolean has(String name) {
        return null != attributes.getObject(name);
    }

    /**
     * 类型
     * @return 类型
     */
    public String type() {
        return get("type", String.class);
    }
}
