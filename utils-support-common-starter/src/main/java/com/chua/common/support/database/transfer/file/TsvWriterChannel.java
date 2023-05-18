package com.chua.common.support.database.transfer.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractWriterChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.univocity.parsers.common.record.Record;
import com.chua.common.support.file.univocity.parsers.common.record.RecordMetaData;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParser;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParserSettings;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Pair;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * csv
 *
 * @author CH
 */
@Spi("tsv")
public class TsvWriterChannel extends AbstractWriterChannel implements InitializingAware, DisposableAware {

    TsvParserSettings tsvParserSettings;
    protected Pair[] header;
    protected String separator;
    protected Pair[] pairs;
    protected String[] headers;
    private TsvParser tsvParser;

    public TsvWriterChannel(Object input) {
        super(input);
    }

    public TsvWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
        super(configuration, inputStream);
    }


    @Override
    public void afterPropertiesSet() {
        this.separator = configuration.separator();
        this.header = configuration.header();
        this.pairs = configuration.header();
        if (null == pairs) {
            pairs = dataMapping.getValuePair();
        }
        this.headers = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            Pair pair = pairs[i];
            headers[i] = pair.getLabel();
        }
        tsvParserSettings = new TsvParserSettings();

        if (!StringUtils.isNullOrEmpty(configuration.emptyValue())) {
            tsvParserSettings.setNullValue(configuration.emptyValue());
        }

        tsvParserSettings.setAutoClosingEnabled(true);
        tsvParserSettings.setSkipEmptyLines(configuration.skipEmptyLines());
        this.tsvParser = new TsvParser(tsvParserSettings);
    }

    @Override
    public void destroy() {
    }


    @Override
    public SinkTable createSinkTable() {
        List<Map<String, Object>> tpl = new LinkedList<>();
        try {
            tsvParser.beginParsing(new InputStreamReader(inputStream, configuration.charset()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        int skip = configuration.skip();
        RecordMetaData recordMetadata = tsvParser.getRecordMetadata();
        int index = 0;
        Record record;
        while ((record = tsvParser.parseNextRecord()) != null) {
            try {
                if (skip > 0 && index < skip) {
                    continue;
                }

                Map<String, Object> stringObjectMap = doAnalysis(recordMetadata, record);
                if(null == stringObjectMap) {
                    continue;
                }
                tpl.add(stringObjectMap);
            } finally {
                index++;
            }
        }
        tsvParser.stopParsing();
        finish();
        return new SinkTable(dataMapping, tpl);
    }


    /**
     * 组装对象
     *
     * @param recordMetadata 媒体
     * @param record         结果
     * @return 返回值
     */
    protected Map<String, Object> doAnalysis(RecordMetaData recordMetadata, Record record) {
        Map<String, Object> item = new LinkedHashMap<>();
        String[] headers1 = recordMetadata.headers();
        for (String s : headers1) {
            Pair pair = dataMapping.getPair(s);
            if(null == pair) {
                continue;
            }
            int index = -1;
            try {
                index = recordMetadata.indexOf(pair.getLabel());
            } catch (Exception ignored) {
                index = recordMetadata.indexOf(pair.getName());
            }

            if(index == -1) {
                continue;
            }
            Object value = record.getValue(index, null);
            item.put(s, value);
        }

        return item;
    }
}
