package com.chua.common.support.file.export;

import com.chua.common.support.json.Json;
import com.chua.common.support.spi.Spi;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json
 *
 * @author CH
 */
@Spi("json_db")
public class JsonDbExportFile extends AbstractExportFile {

    public JsonDbExportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public <T> void export(OutputStream outputStream, List<T> data) {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, configuration.charset())) {
            Map<String, Object> data1 = new HashMap<>(1);
            data1.put("RECORDS", data);
            writer.write(Json.toJson(data1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
