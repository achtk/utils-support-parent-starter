package com.chua.common.support.database.transfer.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.json.Json;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.MapValue;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * sql
 *
 * @author CH
 */
@Spi("json")
public class JsonReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {


    private OutputStreamWriter writer;

    public JsonReaderChannel(Object obj) {
        super(obj);
    }

    public JsonReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        super(configuration, outputStream);
    }

    @Override
    public void read(SinkTable sinkTable) {
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                writer.write(Json.toJson(dataMapping.transferFrom(mapValue)) + ",");
            });
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() {
        try {
            this.writer = createWriter();
            writer.write("[");
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            writer.write("{}]");
            writer.flush();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() throws Exception {
        if (autoClose) {
            IoUtils.closeQuietly(writer);
        }
    }
}
