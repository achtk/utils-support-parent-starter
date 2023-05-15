package com.chua.common.support.collection;


import com.chua.common.support.constant.CommonConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chua.common.support.constant.NumberConstant.DEFAULT_INITIAL_CAPACITY;


/**
 * 层级压缩
 *
 * @author CH
 * @version 1.0.0
 */
public class LevelsClose implements Levels {
    private static final String DEFAULT_ = ".";

    private String sp = DEFAULT_;

    public LevelsClose() {
    }

    public LevelsClose(String sp) {
        this.sp = sp;
    }

    /**
     * 解析map
     *
     * @param map    数据
     * @param result 返回结果集
     */
    private void analysisHierarchicalAnalysis(Map<String, Object> map, final Map<String, Object> result) {
        if (null == map) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            dataFormatProfileHierarchicalAnalysis(key, value, result);
        }
    }

    /**
     * 数据格式化成 配置格式
     *
     * @param parentName  父配置名称
     * @param valueObject 数据
     * @param result      返回对象
     */
    private void dataFormatProfileHierarchicalAnalysis(String parentName, Object valueObject, Map<String, Object> result) {
        if (valueObject instanceof Map) {
            doAnalysisMapValueHierarchicalAnalysis(parentName, (Map<String, Object>) valueObject, result);
        } else if (valueObject instanceof List) {
            doAnalysisListValueHierarchicalAnalysis(parentName, (List<Object>) valueObject, result);
        } else {
            result.put(parentName, valueObject);
        }
    }

    /**
     * 循环解析 Map
     *
     * @param parentName 父配置名称
     * @param map        数据
     * @param result     返回对象
     */
    private void doAnalysisMapValueHierarchicalAnalysis(String parentName, Map<String, Object> map, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            dataFormatProfileHierarchicalAnalysis(parentName + sp + key, value, result);
        }
    }

    /**
     * 循环解析 List
     *
     * @param parentName 父配置名称
     * @param source     数据
     * @param result     返回对象
     */
    private void doAnalysisListValueHierarchicalAnalysis(String parentName, List<Object> source, Map<String, Object> result) {
        for (int i = 0; i < source.size(); i++) {
            dataFormatProfileHierarchicalAnalysis(parentName + CommonConstant.SYMBOL_LEFT_SQUARE_BRACKET + i + CommonConstant.SYMBOL_RIGHT_SQUARE_BRACKET, source.get(i), result);
        }
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
        Map<String, Object> properties1 = new HashMap<>(DEFAULT_INITIAL_CAPACITY);
        analysisHierarchicalAnalysis(stringObjectMap, properties1);
        return properties1;
    }
}
