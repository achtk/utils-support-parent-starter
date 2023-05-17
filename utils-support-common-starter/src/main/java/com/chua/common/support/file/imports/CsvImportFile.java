package com.chua.common.support.file.imports;

import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.univocity.parsers.common.record.Record;
import com.chua.common.support.univocity.parsers.common.record.RecordMetaData;
import com.chua.common.support.univocity.parsers.csv.CsvParser;
import com.chua.common.support.univocity.parsers.csv.CsvParserSettings;
import com.google.common.base.Strings;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * csv
 *
 * @author CH
 */
@Spi("csv")
public class CsvImportFile extends AbstractImportFile {

    CsvParserSettings csvParserSettings;

    public CsvImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, configuration.charset())) {
            CsvParser csvParser = new CsvParser(csvParserSettings);
            csvParser.beginParsing(reader);
            RecordMetaData recordMetadata = csvParser.getRecordMetadata();
            int index = 0;
            Record record;
            while ((record = csvParser.parseNextRecord()) != null) {
                try {
                    if (skip > 0 && index < skip) {
                        continue;
                    }

                    listener.accept(doAnalysis(recordMetadata, type, record));
                    if (listener.isEnd(index)) {
                        csvParser.stopParsing();
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
        csvParserSettings = new CsvParserSettings();

        csvParserSettings.setHeaders(headers);
        if (!Strings.isNullOrEmpty(configuration.emptyValue())) {
            csvParserSettings.setNullValue(configuration.emptyValue());
        }

        csvParserSettings.setAutoClosingEnabled(true);
        csvParserSettings.setSkipEmptyLines(configuration.skipEmptyLines());
    }
}
