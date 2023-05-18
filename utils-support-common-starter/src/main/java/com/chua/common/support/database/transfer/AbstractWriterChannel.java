package com.chua.common.support.database.transfer;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.DataMapping;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 输入
 * @author CH
 */
public abstract class AbstractWriterChannel implements WriterChannel{

    protected final ExportConfiguration configuration;
    protected final InputStream inputStream;
    protected SinkConfig sinkConfig;

    protected DataMapping dataMapping;
    protected boolean autoClose;
    protected AtomicBoolean status = new AtomicBoolean(false);

    public AbstractWriterChannel(Object input) {
        this(new ExportConfiguration(), Converter.convertIfNecessary(input, InputStream.class));
    }


    public AbstractWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
        this.configuration = configuration;
        this.inputStream = inputStream;
    }

    @Override
    public WriterChannel register(SinkConfig sinkConfig) {
        this.sinkConfig = sinkConfig;
        return this;
    }

    @Override
    public WriterChannel register(DataMapping dataMapping) {
        this.dataMapping = dataMapping;
        return this;
    }

    @Override
    public WriterChannel autoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    /**
     * 完成
     */
    public void finish() {
        status.set(true);
    }
    /**
     * 字符
     */
    public InputStreamReader createReader() {
        try {
            return new InputStreamReader(inputStream, configuration.charset());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public boolean isFinish() {
        return status.get();
    }
    @Override
    public void close() throws Exception {
        if (autoClose) {
            IoUtils.closeQuietly(inputStream);
        }
    }
}
