package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.Joiner;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * json
 *
 * @author CH
 */
@Spi("json")
public class JsonExportFile extends AbstractExportFile {

    private OutputStreamWriter writer;

    public JsonExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        try {
            this.writer = new OutputStreamWriter(outputStream, configuration.charset());
            writer.write("[");
            writer.write("[\"");
            writer.write(Joiner.on("\",\"").join(headers));
            writer.write("\"]");
            for (int i = 0; i < data.size(); i++) {
                T datum = data.get(i);
                Object[] array = createArray(datum, true);
                if (null != array) {
                    writer.write(",");
                    writer.write("[\"");
                    writer.write(Joiner.on("\",\"").useForNull("").join(array));
                    writer.write("\"]");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> void append(List<T> records) {
        try {
            for (int i = 0; i < records.size(); i++) {
                T datum = records.get(i);
                Object[] array = createArray(datum, true);
                if (null != array) {
                    writer.write(",");
                    writer.write("[\"");
                    writer.write(Joiner.on("\",\"").useForNull("").join(array));
                    writer.write("\"]");
                }
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public void close() throws Exception {
        writer.write("]");
        writer.flush();
    }
}
