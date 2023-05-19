package com.chua.common.support.file.transfer;

import com.chua.common.support.file.xml.XML;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonObject;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

/**
 * xml -> properties
 *
 * @author CH
 */
public class XmlToPropertiesFileConverter extends AbstractFileConverter {
    @SneakyThrows
    @Override
    public void convert(String type, InputStream sourcePath, String suffix, OutputStream targetPath) {
        String charset = getString("charset", "utf-8");
        JsonObject jsonObject;
        try (InputStreamReader isr = new InputStreamReader(sourcePath, charset)) {
            jsonObject = XML.toJsonObject(isr);
        }

        Properties properties = new Properties();
        Map json = Json.fromJson(jsonObject.toString(), Map.class);
        json.forEach((k, v) -> {
            if (null == v) {
                return;
            }
            properties.put(k, v);

        });

        OutputStreamWriter osw = new OutputStreamWriter(targetPath, charset);
        properties.store(osw, getString("comments", "xmlToProperties"));
    }

    @Override
    public String target() {
        return "json";
    }

    @Override
    public String source() {
        return "xml";
    }
}
