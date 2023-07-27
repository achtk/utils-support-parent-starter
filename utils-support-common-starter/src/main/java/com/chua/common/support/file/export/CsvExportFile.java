package com.chua.common.support.file.export;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.univocity.parsers.csv.CsvParserSettings;
import com.chua.common.support.file.univocity.parsers.csv.CsvWriter;
import com.chua.common.support.file.univocity.parsers.csv.CsvWriterSettings;
import com.chua.common.support.utils.StringUtils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * csv
 *
 * @author CH
 */
@Spi("csv")
public class CsvExportFile extends AbstractExportFile {

    CsvParserSettings csvParserSettings;
    CsvWriterSettings csvWriterSettings;
    private CsvWriter csvWriter;

    public CsvExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {
        csvParserSettings = new CsvParserSettings();
        csvWriterSettings = new CsvWriterSettings();

        csvParserSettings.setHeaders(headers);
        csvWriterSettings.setHeaders(headers);
        if (!StringUtils.isNullOrEmpty(configuration.emptyValue())) {
            csvParserSettings.setNullValue(configuration.emptyValue());
            csvWriterSettings.setNullValue(configuration.emptyValue());
        }

        csvParserSettings.setAutoClosingEnabled(true);
        csvParserSettings.setSkipEmptyLines(configuration.skipEmptyLines());
        csvWriterSettings.setSkipEmptyLines(configuration.skipEmptyLines());

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, configuration.charset());
            this.csvWriter = new CsvWriter(writer, csvWriterSettings);
            csvWriter.writeHeaders(headers);
            for (T datum : data) {
                Object[] array = createArray(datum, true);
                if (null != array) {
                    csvWriter.writeRow(array);
                }
            }
            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void append(List<T> records) {
        for (T datum : records) {
            Object[] array = createArray(datum, true);
            if (null != array) {
                csvWriter.writeRow(array);
            }
        }
        csvWriter.flush();
    }

    @Override
    public void close() throws Exception {
        csvWriter.close();
    }
}
