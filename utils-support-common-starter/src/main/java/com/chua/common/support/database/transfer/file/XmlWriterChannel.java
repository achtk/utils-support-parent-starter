package com.chua.common.support.database.transfer.file;

import com.chua.common.support.aware.DisposableAware;
import com.chua.common.support.aware.InitializingAware;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.database.transfer.AbstractWriterChannel;
import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.file.export.ExportConfiguration;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.value.Pair;
import com.chua.common.support.xml.XML;
import com.chua.common.support.xml.XmlToJSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * xml
 *
 * @author CH
 */
@Spi("xml")
public class XmlWriterChannel extends AbstractWriterChannel implements InitializingAware, DisposableAware {

    protected ExportConfiguration configuration;
    protected OutputStream outputStream;

    public XmlWriterChannel(Object obj) {
        this(new ExportConfiguration(), Converter.convertIfNecessary(obj, InputStream.class));
    }
    public XmlWriterChannel(ExportConfiguration configuration, InputStream inputStream) {
        super(configuration, inputStream);
    }


    @Override
    public void destroy() {

    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public SinkTable createSinkTable() {
        JsonObject jsonObject = XML.toJsonObject(createReader());
        JsonObject data = jsonObject.getJsonObject("data");
        JsonArray jsonArray = data.getJsonArray("item");
        List<Map<String, Object>> tpl = new LinkedList<>();
        jsonArray.forEach(it -> {
            Map<String, Object> item = new LinkedHashMap<>();
            XmlToJSONObject xml = (XmlToJSONObject) it;
            xml.forEach((k, v) -> {
                XmlToJSONObject v1 = (XmlToJSONObject) v;
                Pair pair = dataMapping.getPair(k);
                if(null == pair) {
                    pair = dataMapping.getPair(v1.get("describe").toString());
                }

                if(null == pair) {
                    return;
                }

                item.put(pair.getName(), v1.get("content"));
            });
            tpl.add(item);
        });

        finish();
        return new SinkTable(dataMapping, tpl);
    }
}
