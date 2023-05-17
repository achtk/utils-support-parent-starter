package com.chua.common.support.file.imports;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.univocity.parsers.csv.CsvParserSettings;
import com.chua.common.support.file.xml.JSONArray;
import com.chua.common.support.file.xml.XML;
import com.chua.common.support.json.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * csv
 * xml
 *
 * @author CH
 */
@Spi("xml")
public class XmlImportFile extends AbstractImportFile {

    CsvParserSettings csvParserSettings;

    public XmlImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        JsonObject jsonObject = null;
        try (InputStreamReader reader = new InputStreamReader(inputStream, configuration.charset())) {
            jsonObject = XML.toJsonObject(reader, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == jsonObject) {
            return;
        }

        doAnalysisMultiKey(jsonObject, type, listener);
    }

    /**
     * 分析对象
     *
     * @param jsonObject 对象
     * @param type       类型
     * @param listener   监听
     */
    private <T> void doAnalysisMultiKey(JsonObject jsonObject, Class<T> type, ImportListener<T> listener) {
        Set<String> strings = jsonObject.keySet();
        for (String string : strings) {
            Object o = jsonObject.getObject(string);
            if (o instanceof JSONArray) {
                doAnalysisCollection((JSONArray) o, type, listener);
                continue;
            }

            if (o instanceof JsonObject) {
                doAnalysisMultiKey((JsonObject) o, type, listener);
            }
        }

    }


    /**
     * 解析数组
     *
     * @param jsonArray 数组
     * @param type      类型
     * @param listener  监听
     */
    private <T> void doAnalysisCollection(JSONArray jsonArray, Class<T> type, ImportListener<T> listener) {
        int size = jsonArray.length();
        skip = skip - 1;
        for (int i = 0; i < size; i++) {
            if (skip > 0 && i < skip) {
                continue;
            }

            listener.accept(doAnalysis(type, jsonArray.getJSONObject(i)));
            if (listener.isEnd(i + 1)) {
                break;
            }
        }
    }


    @Override
    public void afterPropertiesSet() {
    }
}
