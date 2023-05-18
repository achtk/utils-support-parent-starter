package com.chua.common.support.database.transfer;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.value.DataMapping;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 输入
 * @author CH
 */
public abstract class AbstractReaderChannel implements ReaderChannel{

    protected final ExportConfiguration configuration;
    protected final OutputStream outputStream;
    protected SinkConfig sinkConfig;

    protected DataMapping dataMapping;
    protected boolean autoClose;
    public AbstractReaderChannel(Object obj) {
        this(new ExportConfiguration(), Converter.convertIfNecessary(obj, OutputStream.class));
    }
    public AbstractReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        this.configuration = configuration;
        this.outputStream = outputStream;
    }

    /**
     * 字符
     */
    public OutputStreamWriter createWriter() {
        try {
            return new OutputStreamWriter(outputStream, configuration.charset());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReaderChannel register(SinkConfig sinkConfig) {
        this.sinkConfig = sinkConfig;
        return this;
    }

    @Override
    public ReaderChannel register(DataMapping dataMapping) {
        this.dataMapping = dataMapping;
        return this;
    }

    @Override
    public ReaderChannel autoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }
}
