package com.chua.common.support.database.transfer.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.database.transfer.datasource.DataSourceReaderChannel;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.univocity.parsers.csv.CsvWriter;
import com.chua.common.support.file.univocity.parsers.csv.CsvWriterSettings;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.MapValue;
import com.chua.common.support.value.Pair;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * csv
 * @author CH
 */
@Spi("csv")
public class CsvReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {

    CsvWriterSettings csvWriterSettings;
    protected Pair[] header;
    protected String separator;
    protected Pair[] pairs;
    protected String[] headers;
    private OutputStreamWriter writer;
    private CsvWriter csvWriter;

    public CsvReaderChannel(Object obj) {
        super(obj);
    }

    public CsvReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        super(configuration, outputStream);
    }


    @Override
    public void read(SinkTable sinkTable) {
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                Object[] array = DataSourceReaderChannel.createArgs(dataMapping, null, mapValue);
                csvWriter.writeRow(array);
            });
            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.separator = configuration.separator();
        this.header = configuration.header();
        this.pairs = configuration.header();
        if(null == pairs) {
            pairs = dataMapping.getValuePair();
        }
        this.headers = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            Pair pair = pairs[i];
            headers[i] = pair.getLabel();
        }
        csvWriterSettings = new CsvWriterSettings();

        csvWriterSettings.setHeaders(headers);
        if (!StringUtils.isNullOrEmpty(configuration.emptyValue())) {
            csvWriterSettings.setNullValue(configuration.emptyValue());
        }

        csvWriterSettings.setSkipEmptyLines(configuration.skipEmptyLines());
        this.writer = createWriter();
        this.csvWriter = new CsvWriter(writer, csvWriterSettings);
        csvWriter.writeHeaders(headers);
        csvWriter.flush();
    }

    @Override
    public void destroy() {
        csvWriter.flush();
    }

    @Override
    public void close() throws Exception {
        if(autoClose) {
            csvWriter.close();
            IoUtils.closeQuietly(writer);
        }
    }
}
