package com.chua.elasticsearch.support.collector;

import java.util.List;

/**
 * es采集器
 * @author CH
 */
public interface ElasticSearchCollector extends AutoCloseable {
    /**
     * 创建index
     *
     * @param index 索引
     * @return 结果
     */
    boolean createIndex(String index);
    /**
     * 删除index
     *
     * @param index 索引
     * @return 结果
     */
    boolean deleteIndex(String index);
    /**
     * 设置index的mapping（设置数据类型和分词方式）
     *
     * @param index         索引
     * @param type          类型
     * @param mappingString 映射
     * @return 结果
     */
    boolean createIndexMapping(String index, String type, String mappingString);
    /**
     * 获取index的mapping
     *
     * @param index 索引
     * @param type  类型
     * @return 映射
     */
    String getMapping(String index, String type);
    /**
     * 获取索引index设置setting
     *
     * @param index 索引
     * @return 结果
     */
    boolean getIndexSettings(String index);
    /**
     * 更改索引index设置setting
     *
     * @param index  索引
     * @param source 配置
     * @return 结果
     */
    boolean updateIndexSettings(String index, Object source);
    /**
     * 获取索引 别名
     *
     * @param index 索引
     * @return 别名
     */
    String getIndexAliases(String index);
    /**
     * 添加索引别名
     *
     * @param index 索引
     * @param alias 别名
     * @return 结果
     */
    boolean addAlias(List<String> index, String alias);
    /**
     * 获取索引模版
     *
     * @param template 模版
     * @return 模版
     */
    String getTemplate(String template);
    /**
     * 添加索引模版
     *
     * @param template 模板
     * @param source   数据
     * @return 结果
     */
    String putReturnReportTemplate(String template, Object source);
    /**
     * 索引优化
     */
    void optimizeIndex();
    /**
     * 清理缓存
     */
    void clearCache();
}
