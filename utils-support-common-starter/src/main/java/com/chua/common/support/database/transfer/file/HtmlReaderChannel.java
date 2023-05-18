package com.chua.common.support.database.transfer.file;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.database.transfer.AbstractReaderChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.file.export.XhtmlExportFile;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.SafeConsumer;
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
@Spi("html")
public class HtmlReaderChannel extends AbstractReaderChannel implements InitializingAware, DisposableAware {

    private OutputStreamWriter writer;

    private XhtmlExportFile xhtmlExportFile;

    public HtmlReaderChannel(Object obj) {
        super(obj);
        this.xhtmlExportFile = new XhtmlExportFile(configuration);
    }

    public HtmlReaderChannel(ExportConfiguration configuration, OutputStream outputStream) {
        super(configuration, outputStream);
        this.xhtmlExportFile = new XhtmlExportFile(configuration);
    }


    @Override
    public synchronized void read(SinkTable sinkTable) {
        try {
            sinkTable.flow((SafeConsumer<MapValue>) mapValue -> {
                StringBuffer buffer = new StringBuffer();
                buffer.append("<tr>");
                Map<String, Pair> mapping = dataMapping.getMapping();
                for (Map.Entry<String, Pair> entry : mapping.entrySet()) {
                    Object o = mapValue.get(entry.getKey());
                    xhtmlExportFile.doAnalysisValue(buffer, entry.getValue(), o);

                }
                buffer.append("</tr>");
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
        buffer.append("<html><head><style>")
                .append("p { max-width: 165px;\n" +
                        "    overflow: hidden;\n" +
                        "    white-space: nowrap;\n" +
                        "    text-overflow: ellipsis;" +
                        "}\n" +
                        "table{\n" +
                        "border-collapse: collapse;\n" +
                        "width: 100%;\n" +
                        "}\n" +
                        "th, td{\n" +
                        "text-align: left;\n" +
                        "padding: 8px;\n" +
                        "}\n" +
                        "tr:nth-child(even){\n" +
                        "background-color: #fafafa;\n" +
                        "}\n" +
                        "th{\n" +
                        "background-color: #7799AA;\n" +
                        "color: white;\n" +
                        "}.table {\n" +
                        "    --bs-table-bg: transparent;\n" +
                        "    --bs-table-accent-bg: transparent;\n" +
                        "    --bs-table-striped-color: #212529;\n" +
                        "    --bs-table-striped-bg: rgba(0, 0, 0, 0.05);\n" +
                        "    --bs-table-active-color: #212529;\n" +
                        "    --bs-table-active-bg: rgba(0, 0, 0, 0.1);\n" +
                        "    --bs-table-hover-color: #212529;\n" +
                        "    --bs-table-hover-bg: rgba(0, 0, 0, 0.075);\n" +
                        "    width: 100%;\n" +
                        "    margin-bottom: 1rem;\n" +
                        "    color: #212529;\n" +
                        "    vertical-align: top;\n" +
                        "    border-color: #dee2e6;\n" +
                        "}table {\n" +
                        "    caption-side: bottom;\n" +
                        "    border-collapse: collapse;\n" +
                        "}.table-bordered>:not(caption)>*>* {\n" +
                        "    border-width: 0 1px;\n" +
                        "}\n" +
                        "table tbody {\n" +
                        "\tdisplay: block;\n" +
                        "\theight: calc(800px - 39px);\n" +
                        "\toverflow-y: scroll;\n" +
                        "}\n" +
                        " \n" +
                        "table thead, tbody tr {\n" +
                        "\tdisplay: table;\n" +
                        "\twidth: 100%;\n" +
                        "\ttable-layout: fixed;\n" +
                        "}\n" +
                        " \n" +
                        "table thead {\n" +
                        "\twidth: calc(100% - 1em)\n" +
                        "}" +
                        ".table>:not(caption)>*>* {\n" +
                        "    padding: 0.5rem 0.5rem;\n" +
                        "    border-bottom-width: 1px;\n" +
                        "    box-shadow: inset 0 0 0 9999px var(--bs-table-accent-bg);\n" +
                        "}.table>:not(:first-child) {\n" +
                        "    border-top: 2px solid currentColor;\n" +
                        "}\n" +
                        "\n" +
                        ".table>tbody {\n" +
                        "    vertical-align: inherit;\n" +
                        "}\n" +
                        "tbody, td, tfoot, th, thead, tr {\n" +
                        "    border-color: inherit;\n" +
                        "    border-style: solid;\n" +
                        "    border-width: 0;\n" +
                        "}tbody, td, tfoot, th, thead, tr {\n" +
                        "    border-color: inherit;\n" +
                        "    border-style: solid;\n" +
                        "    border-width: 0;\n" +
                        "}")
                .append("</style></head><body><table class='table'><thead><tr>");
        Pair[] pairs = dataMapping.getValuePair();

        String[] headers = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            Pair pair = pairs[i];
            headers[i] = pair.getLabel();
        }
        for (String header : headers) {
            buffer.append("<th>").append(header).append("</th>");
        }
        buffer.append("</tr></thead><tbody class='1'>");

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
        StringBuffer buffer = new StringBuffer();
        buffer.append("</tbody></table></body>");
        buffer.append("</html>");
        try {
            this.writer.write(buffer.toString());
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
