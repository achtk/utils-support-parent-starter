package com.chua.elasticsearch.support.collector;

import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.indices.ClearCache;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.Optimize;
import io.searchbox.indices.aliases.AddAliasMapping;
import io.searchbox.indices.aliases.GetAliases;
import io.searchbox.indices.aliases.ModifyAliases;
import io.searchbox.indices.mapping.GetMapping;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.indices.settings.GetSettings;
import io.searchbox.indices.settings.UpdateSettings;
import io.searchbox.indices.template.GetTemplate;
import io.searchbox.indices.template.PutTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * es采集器
 *
 * @author CH
 */
@Slf4j
public class SimpleElasticSearchCollector
        implements ElasticSearchCollector {
    private final JestClient jestClient;

    public SimpleElasticSearchCollector(JestClient jestClient) {
        this.jestClient = jestClient;
    }

    /**
     * 创建index
     *
     * @param index 索引
     * @return 结果
     */
    @Override
    public boolean createIndex(String index) {
        try {
            JestResult jestResult = jestClient.execute(new CreateIndex.Builder(index).build());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除index
     *
     * @param index 索引
     * @return 结果
     */
    @Override
    public boolean deleteIndex(String index) {
        try {
            JestResult jestResult = jestClient.execute(new DeleteIndex.Builder(index).build());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置index的mapping（设置数据类型和分词方式）
     *
     * @param index         索引
     * @param type          类型
     * @param mappingString 映射
     * @return 结果
     */
    @Override
    public boolean createIndexMapping(String index, String type, String mappingString) {
        //mappingString为拼接好的json格式的mapping串
        PutMapping.Builder builder = new PutMapping.Builder(index, type, mappingString);
        try {
            JestResult jestResult = jestClient.execute(builder.build());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取index的mapping
     *
     * @param index 索引
     * @param type  类型
     * @return 映射
     */
    @Override
    public String getMapping(String index, String type) {
        GetMapping.Builder builder = new GetMapping.Builder();
        builder.addIndex(index).addType(type);
        try {
            JestResult result = jestClient.execute(builder.build());
            if (result != null && result.isSucceeded()) {
                return result.getSourceAsObject(JsonObject.class).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取索引index设置setting
     *
     * @param index 索引
     * @return 结果
     */
    @Override
    public boolean getIndexSettings(String index) {
        try {
            JestResult jestResult = jestClient.execute(new GetSettings.Builder().addIndex(index).build());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更改索引index设置setting
     *
     * @param index  索引
     * @param source 配置
     * @return 结果
     */
    @Override
    public boolean updateIndexSettings(String index, Object source) {
        try {
            JestResult jestResult = jestClient.execute(new UpdateSettings.Builder(source).build());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取索引 别名
     *
     * @param index 索引
     * @return 别名
     */
    @Override
    public String getIndexAliases(String index) {
        try {
            JestResult jestResult = jestClient.execute(new GetAliases.Builder().addIndex(index).build());
            return jestResult.getJsonString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加索引别名
     *
     * @param index 索引
     * @param alias 别名
     * @return 结果
     */
    @Override
    public boolean addAlias(List<String> index, String alias) {
        try {
            AddAliasMapping build = new AddAliasMapping.Builder(index, alias).build();
            JestResult jestResult = jestClient.execute(new ModifyAliases.Builder(build).build());
            return jestResult.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取索引模版
     *
     * @param template 模版
     * @return 模版
     */
    @Override
    public String getTemplate(String template) {
        try {
            JestResult jestResult = jestClient.execute(new GetTemplate.Builder(template).build());
            return jestResult.getJsonString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return template;
    }

    /**
     * 添加索引模版
     *
     * @param template 模板
     * @param source   数据
     * @return 结果
     */
    @Override
    public String putReturnReportTemplate(String template, Object source) {
        JestResult jestResult = null;
        try {
            jestResult = jestClient.execute(new PutTemplate.Builder(template, source).build());
            return jestResult.getJsonString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 索引优化
     */
    @Override
    public void optimizeIndex() {
        Optimize optimize = new Optimize.Builder().build();
        jestClient.executeAsync(optimize, new JestResultHandler<JestResult>() {
            public void completed(JestResult jestResult) {
            }

            public void failed(Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 清理缓存
     */
    @Override
    public void clearCache() {
        try {
            ClearCache clearCache = new ClearCache.Builder().build();
            jestClient.execute(clearCache);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws Exception {
        jestClient.close();
    }
}
