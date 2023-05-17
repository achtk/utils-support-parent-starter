package com.chua.common.support.file.imports;

import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.IoUtils;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * json
 *
 * @author CH
 */
@Spi("json")
public class JsonImportFile extends AbstractImportFile {


    public JsonImportFile(ExportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener) {
        JsonArray jsonArray = null;
        try (InputStreamReader reader = new InputStreamReader(inputStream, configuration.charset())) {
            jsonArray = Json.getJsonArray(IoUtils.toString(reader));
        } catch (Exception ignore) {
        }
        if (null == jsonArray) {
            return;
        }

        int size = jsonArray.size();
        JsonArray head = jsonArray.getJsonArray(0);
        for (int i = 0; i < size; i++) {
            if (skip > 0 && i < skip) {
                continue;
            }

            listener.accept(doAnalysis(head, type, jsonArray.getJsonArray(i)));
            if (listener.isEnd(i)) {
                break;
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
    }

}
