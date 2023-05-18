package com.chua.common.support.database.transfer.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.database.transfer.datasource.DataSourceReaderChannel;
import com.chua.common.support.file.univocity.parsers.tsv.TsvParserSettings;
import com.chua.common.support.file.univocity.parsers.tsv.TsvWriter;
import com.chua.common.support.file.univocity.parsers.tsv.TsvWriterSettings;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.MapValue;
import com.chua.common.support.value.Pair;

import java.io.OutputStreamWriter;

/**
 * tsv
 * @author CH
 */
@Spi("tsv")
public class TsvReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {

    TsvParserSettings tsvParserSettings;
    TsvWriterSettings tsvWriterSettings;
    protected Pair[] header;
    protected String separator;
    protected Pair[] pairs;
    protected String[] headers;
    private OutputStreamWriter writer;
    private TsvWriter tsvWriter;

    public TsvReaderChannel(Object obj) {
        super(obj);
    }


    @Override
    public void read(SinkTable sinkTable) {
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                Object[] array = DataSourceReaderChannel.createArgs(dataMapping, null, mapValue);
                tsvWriter.writeRow(array);
            });
            tsvWriter.flush();
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
        this.writer = createWriter();
        this.tsvWriter = new TsvWriter(writer, tsvWriterSettings);
        tsvWriter.writeHeaders(headers);
    }

    @Override
    public void destroy() {
        tsvWriter.flush();
    }

    @Override
    public void close() throws Exception {
        if(autoClose) {
            tsvWriter.close();
            IoUtils.closeQuietly(writer);
        }
    }
}
