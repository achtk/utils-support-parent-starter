package com.chua.common.support.database.transfer.file;

import com.chua.common.support.aware.DisposableAware;
import com.chua.common.support.aware.InitializingAware;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.export.XmlExportFile;
import com.chua.common.support.function.SafeConsumer;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.value.MapValue;
import com.chua.common.support.value.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * xml
 *
 * @author CH
 */
@Spi("xml")
public class XmlReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {

    private OutputStreamWriter writer;

    private XmlExportFile xmlExportFile;

    public XmlReaderChannel(Object obj) {
        super(obj);
        this.xmlExportFile  = new XmlExportFile(configuration);
    }

    public XmlReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        super(configuration, outputStream);
        this.xmlExportFile  = new XmlExportFile(configuration);
    }


    @Override
    public synchronized void read(SinkTable sinkTable) {
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                StringBuffer buffer = new StringBuffer();
                buffer.append("<item>");
                Map<String, Pair> mapping = dataMapping.getMapping();
                for (Map.Entry<String, Pair> entry : mapping.entrySet()) {
                    Object o = mapValue.get(entry.getKey());
                    xmlExportFile.doAnalysisValue(buffer, entry.getValue(), o);

                }
                buffer.append("</item>");
                writer.write(buffer.toString());
                writer.flush();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"").append(configuration.charset()).append("\"?>");
        buffer.append("<data>");
        try {
            this.writer = createWriter();
            this.writer.write(buffer.toString());
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            this.writer.write("</data>");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (autoClose) {
            IoUtils.closeQuietly(writer);
        }
    }
}
