package com.chua.common.support.database.transfer.file;

import com.alibaba.fastjson2.JSONPath;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.database.transfer.AbstractWriterChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * json
 *
 * @author CH
 */
@Spi("json")
public class JsonWriterChannel extends AbstractWriterChannel implements InitializingAware, DisposableAware {

    protected ExportConfiguration configuration;
    protected InputStream inputStream;
    private String subKey;

    public JsonWriterChannel(Object input) {
        super(input);
    }

    public JsonWriterChannel(Object o, String subKey) {
        this(new ExportConfiguration(), Converter.convertIfNecessary(o, InputStream.class), subKey);
    }

    public JsonWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
        super(configuration, inputStream);
    }

    public JsonWriterChannel(ExportConfiguration configuration, InputStream inputStream, String subKey) {
        super(configuration, inputStream);
        this.subKey = subKey;
    }

    @Override
    public void afterPropertiesSet() {
    }


    @Override
    public void destroy() {
        try {
            close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SinkTable createSinkTable() {
        JsonArray jsonArray = null;
        if(StringUtils.isNullOrEmpty(subKey)) {
            try {
                jsonArray = Json.getJsonArray(IoUtils.toString(inputStream, UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                jsonArray = new JsonArray((Collection) JSONPath.extract(IoUtils.toString(inputStream, UTF_8), subKey));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        List<Map<String, Object>> list = new LinkedList<>();
        jsonArray.forEach(it -> {
            Map<String, Object> stringObjectMap = dataMapping.transferFrom(BeanMap.create(it));
            if(stringObjectMap.isEmpty()) {
                return;
            }
            list.add(stringObjectMap);
        });
        finish();
        return new SinkTable(dataMapping, list);
    }
}
