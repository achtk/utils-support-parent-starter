package com.chua.common.support.file.transfer;

import com.chua.common.support.collection.FlatMap;
import com.chua.common.support.json.Json;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * json -> properties
 *
 * @author CH
 */
public class JsonToPropertiesFileConverter extends AbstractFileConverter {
    @SneakyThrows
    @Override
    public void convert(String type, InputStream sourcePath, String suffix, OutputStream targetPath) {
        Object value;
        try {
            value = Json.fromJson(new InputStreamReader(sourcePath, UTF_8), Map.class);
        } catch (Exception e) {
            value = Json.fromJson(new InputStreamReader(sourcePath, UTF_8), Collection.class);
        }

        FlatMap flatMap = FlatMap.create();
        if (null != value) {
            if (value instanceof Map) {
                flatMap.putAll((Map<? extends String, ?>) value);
            } else {
                ((Collection) value).forEach(flatMap::put);
            }
        }

        Properties properties = new Properties();
        properties.putAll(flatMap);

        properties.store(new OutputStreamWriter(targetPath, UTF_8), "");

    }

    @Override
    public String target() {
        return "properties";
    }

    @Override
    public String source() {
        return "json";
    }
}
