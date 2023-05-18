package com.chua.common.support.database.transfer.file;

import com.chua.common.support.aware.DisposableAware;
import com.chua.common.support.aware.InitializingAware;
import com.chua.common.support.database.transfer.AbstractWriterChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.univocity.parsers.common.record.Record;
import com.chua.common.support.univocity.parsers.common.record.RecordMetaData;
import com.chua.common.support.univocity.parsers.csv.CsvParser;
import com.chua.common.support.univocity.parsers.csv.CsvParserSettings;
import com.chua.common.support.value.Pair;
import com.google.common.base.Strings;

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
@Spi("csv")
public class CsvWriterChannel extends AbstractWriterChannel implements InitializingAware, DisposableAware {

    CsvParserSettings csvParserSettings;
    protected Pair[] header;
    protected String separator;
    protected Pair[] pairs;
    protected String[] headers;
    private CsvParser csvParser;

    public CsvWriterChannel(Object input) {
        super(input);
    }

    public CsvWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
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
        csvParserSettings = new CsvParserSettings();

        if (!Strings.isNullOrEmpty(configuration.emptyValue())) {
            csvParserSettings.setNullValue(configuration.emptyValue());
        }

        csvParserSettings.setAutoClosingEnabled(true);
        csvParserSettings.setSkipEmptyLines(configuration.skipEmptyLines());
        this.csvParser = new CsvParser(csvParserSettings);
    }

    @Override
    public void destroy() {
    }

    @Override
    public SinkTable createSinkTable() {
        List<Map<String, Object>> tpl = new LinkedList<>();
        try {
            csvParser.beginParsing(new InputStreamReader(inputStream, configuration.charset()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        int skip = configuration.skip();
        RecordMetaData recordMetadata = csvParser.getRecordMetadata();
        int index = 0;
        Record record;
        while ((record = csvParser.parseNextRecord()) != null) {
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
        csvParser.stopParsing();
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
