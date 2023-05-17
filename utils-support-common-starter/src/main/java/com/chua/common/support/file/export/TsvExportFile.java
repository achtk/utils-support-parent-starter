package com.chua.common.support.file.export;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParserSettings;
import com.chua.common.support.file.univocity.parsers.tsv.TsvWriter;
import com.chua.common.support.file.univocity.parsers.tsv.TsvWriterSettings;
import com.chua.common.support.utils.StringUtils;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * tsv
 *
 * @author CH
 */
@Spi("tsv")
public class TsvExportFile extends AbstractExportFile {

    TsvParserSettings tsvParserSettings;
    TsvWriterSettings tsvWriterSettings;

    public TsvExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {
        tsvParserSettings = new TsvParserSettings();
        tsvWriterSettings = new TsvWriterSettings();

        tsvParserSettings.setHeaders(headers);
        tsvWriterSettings.setHeaders(headers);
        if (!StringUtils.isNullOrEmpty(configuration.emptyValue())) {
            tsvParserSettings.setNullValue(configuration.emptyValue());
            tsvWriterSettings.setNullValue(configuration.emptyValue());
        }

        tsvParserSettings.setAutoClosingEnabled(true);
        tsvParserSettings.setSkipEmptyLines(configuration.skipEmptyLines());
        tsvWriterSettings.setSkipEmptyLines(configuration.skipEmptyLines());

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, configuration.charset())) {
            TsvWriter tsvWriter = new TsvWriter(writer, tsvWriterSettings);
            tsvWriter.writeHeaders(headers);
            for (T datum : data) {
                Object[] array = createArray(datum, true);
                if (null != array) {
                    tsvWriter.writeRow(array);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
