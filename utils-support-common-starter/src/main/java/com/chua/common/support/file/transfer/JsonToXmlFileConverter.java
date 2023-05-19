package com.chua.common.support.file.transfer;

import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonArray;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.utils.IoUtils;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * json -> xml
 *
 * @author CH
 */
public class JsonToXmlFileConverter extends AbstractFileConverter {
    private static final String JSON_OBJECT = "com.alibaba.fastjson.JSONObject";
    private static final String JSON_ARRAY = "com.alibaba.fastjson.JSONArray";
    private static final String STRING = "java.lang.String";
    private static final Pattern PATTERN = Pattern.compile("[<>&\"',]");

    @SneakyThrows
    @Override
    public void convert(String type, InputStream sourcePath, String suffix, OutputStream targetPath) {
        String charset = getString("charset", "utf-8");

        String xml;
        try (InputStreamReader isr = new InputStreamReader(sourcePath, charset)) {
            xml = jsonStr2Xml(IoUtils.toString(isr));
        }

        IoUtils.write(xml, targetPath, charset);
    }

    /**
     * jsonStr2Xml
     *
     * @param json json
     * @return java.lang.String
     */
    public String jsonStr2Xml(String json) {
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            JsonObject jObj = Json.getJsonObject(json);
            json2Xml(jObj, buffer);
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * Json to xmlstr string
     *
     * @param jObj   the j obj
     * @param buffer the buffer
     * @return the string
     */
    public static String json2Xml(JsonObject jObj, StringBuffer buffer) {
        Set<Map.Entry<String, Object>> se = jObj.entrySet();
        for (Iterator<Map.Entry<String, Object>> it = se.iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> en = it.next();
            if (en.getValue().getClass().getName().equals(JSON_OBJECT)) {
                buffer.append("<").append(en.getKey()).append(">");
                JsonObject jo = jObj.getJsonObject(en.getKey());
                json2Xml(jo, buffer);
                buffer.append("</").append(en.getKey()).append(">");
            } else if (en.getValue().getClass().getName().equals(JSON_ARRAY)) {
                JsonArray jarray = jObj.getJsonArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append("<").append(en.getKey()).append(">");
                    JsonObject jsonobject = jarray.getJsonObject(i);
                    json2Xml(jsonobject, buffer);
                    buffer.append("</").append(en.getKey()).append(">");
                }
            } else if (en.getValue().getClass().getName().equals(STRING)) {
                buffer.append("<").append(en.getKey()).append(">").append(escape((String) en.getValue()));
                buffer.append("</").append(en.getKey()).append(">");
            }
        }
        return buffer.toString();
    }

    private static String escape(String string) {
        return PATTERN.matcher(string).find() ? "<![CDATA[" + string + "]]>" : string;
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
