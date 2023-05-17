package com.chua.common.support.file.export;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.utils.NetAddress;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.value.Pair;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * rdf
 *
 * @author CH
 */
@Spi("rdf")
public class RdfExportFile extends XmlExportFile {

    private static final String META = "meta";

    public RdfExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        String rdfUri = configuration.rdfUri();
        NetAddress netAddress = NetAddress.of(rdfUri);

        StringBuffer buffer = new StringBuffer();
        buffer.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
        buffer.append(" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        buffer.append("  xmlns:").append(META).append("=\"").append(netAddress.getProtocol("http"))
                .append("://").append(netAddress.getAddress())
                .append("/rdf/3.0#")
                .append("\"");
        buffer.append(">");
        for (T datum : data) {
            buffer.append("<rdf:Description rdf:about=\"").append(rdfUri).append("\">");
            doAnalysis(buffer, datum);
            buffer.append("</rdf:Description>");
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, configuration.charset())) {
            writer.write(buffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 生成xml
     *
     * @param buffer 结果
     * @param pair   详情
     * @param value  数据
     */
    @Override
    public void doAnalysisValue(StringBuffer buffer, Pair pair, Object value) {
        buffer.append("<").append(META).append(":").append(pair.getName());
        String label = pair.getLabel();
        if (!StringUtils.isNullOrEmpty(label) && !pair.getName().equals(label)) {
            buffer.append(" describe=\"").append(pair.getLabel()).append("\"");
        }
        buffer.append(">");
        buffer.append(converterType(pair, value));
        buffer.append("</").append(pair.getName());
        buffer.append(">");
    }


}
