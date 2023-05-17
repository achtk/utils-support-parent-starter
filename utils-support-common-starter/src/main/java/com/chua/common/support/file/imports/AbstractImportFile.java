package com.chua.common.support.file.imports;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.export.resolver.NamedResolver;
import com.chua.common.support.file.univocity.parsers.common.record.Record;
import com.chua.common.support.file.univocity.parsers.common.record.RecordMetaData;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.value.Pair;

/**
 * 导入文件
 *
 * @author CH
 */
public abstract class AbstractImportFile implements ImportFile, InitializingAware {

    protected final String separator;
    private final Pair[] header;
    protected final String[] headers;
    protected final Pair[] pairs;
    protected int skip;
    protected ExportConfiguration configuration;

    public AbstractImportFile(ExportConfiguration configuration) {
        this.configuration = configuration;
        this.separator = configuration.separator();
        this.header = configuration.header();
        this.pairs = configuration.header();
        if (null != pairs) {
            this.headers = new String[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                Pair pair = pairs[i];
                headers[i] = pair.getLabel();
            }
        } else {
            this.headers = new String[0];
        }
        this.skip = configuration.skip();
        afterPropertiesSet();
    }

    /**
     * 组装对象
     *
     * @param type       类型
     * @param jsonObject 对象
     * @param <T>        类型
     * @return 结果
     */
    protected <T> T doAnalysis(Class<T> type, JsonObject jsonObject) {
        return BeanUtils.copyProperties(jsonObject, type);
    }

    /**
     * 组装对象
     *
     * @param header    媒体
     * @param type      类型
     * @param jsonArray 结果
     * @param <T>       类型
     * @return 返回值
     */
    protected <T> T doAnalysis(JsonArray header, Class<T> type, JsonArray jsonArray) {
        T forObject = ClassUtils.forObject(type);
        if (null == forObject) {
            return null;
        }

        NamedResolver namedResolver = configuration.namedResolver();
        ClassUtils.doWithFields(type, field -> {

            Pair pair = namedResolver.name(field);
            int index = -1;
            try {
                index = header.indexOf(pair.getLabel());
            } catch (Exception ignored) {
                index = header.indexOf(pair.getName());
            }

            if (-1 == index) {
                return;
            }

            Object value = jsonArray.get(index);

            if (null == value) {
                return;
            }

            ClassUtils.setAllFieldValue(field, value, type, forObject);
        });
        return forObject;
    }

    /**
     * 组装对象
     *
     * @param recordMetadata 媒体
     * @param type           类型
     * @param record         结果
     * @param <T>            类型
     * @return 返回值
     */
    protected <T> T doAnalysis(RecordMetaData recordMetadata, Class<T> type, Record record) {
        T forObject = ClassUtils.forObject(type);
        if (null == forObject) {
            return null;
        }

        NamedResolver namedResolver = configuration.namedResolver();
        ClassUtils.doWithFields(type, field -> {

            Pair pair = namedResolver.name(field);
            int index = -1;
            try {
                index = recordMetadata.indexOf(pair.getLabel());
            } catch (Exception ignored) {
                index = recordMetadata.indexOf(pair.getName());
            }

            if (-1 == index) {
                return;
            }

            Object value = record.getValue(index, null);

            if (null == value) {
                return;
            }

            ClassUtils.setAllFieldValue(field, value, type, forObject);
        });
        return forObject;
    }
}
