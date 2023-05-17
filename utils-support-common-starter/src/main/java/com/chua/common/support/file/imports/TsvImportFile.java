package com.chua.common.support.file.imports;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.univocity.parsers.common.record.Record;
import com.chua.common.support.file.univocity.parsers.common.record.RecordMetaData;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParser;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParserSettings;
import com.chua.common.support.utils.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * tsv
 *
 * @author CH
 */
@Spi("tsv")
public class TsvImportFile extends AbstractImportFile {

    TsvParserSettings tsvParserSettings;

    public TsvImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, configuration.charset())) {
            TsvParser tsvParser = new TsvParser(tsvParserSettings);
            tsvParser.beginParsing(reader);
            RecordMetaData recordMetadata = tsvParser.getRecordMetadata();
            int index = 0;
            Record record;
            while ((record = tsvParser.parseNextRecord()) != null) {
                try {
                    if (skip > 0 && index < skip) {
                        continue;
                    }

                    listener.accept(doAnalysis(recordMetadata, type, record));
                    if (listener.isEnd(index)) {
                        tsvParser.stopParsing();
                        break;
                    }
                } finally {
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void afterPropertiesSet() {
        tsvParserSettings = new TsvParserSettings();

        tsvParserSettings.setHeaders(headers);
        if (!StringUtils.isNullOrEmpty(configuration.emptyValue())) {
            tsvParserSettings.setNullValue(configuration.emptyValue());
        }

        tsvParserSettings.setAutoClosingEnabled(true);
        tsvParserSettings.setSkipEmptyLines(configuration.skipEmptyLines());
    }
}
