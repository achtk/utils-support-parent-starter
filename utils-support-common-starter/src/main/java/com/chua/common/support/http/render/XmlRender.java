package com.chua.common.support.http.render;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.http.HttpConstant;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 渲染器
 *
 * @author CH
 */
@Spi(HttpConstant.TEXT_XML)
public class XmlRender implements Render {
    @Override
    public byte[] render(Object param, String contentType) {
        Map<String, Object> params = (Map) param;
        List<String> line = new LinkedList<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            doLine(line, entry);
        }

        return Joiner.on("\r\n").join(line).getBytes(StandardCharsets.UTF_8);
    }

    private void doLine(List<String> line, Map.Entry<String, Object> entry) {
        String key = entry.getKey();
        Object value = entry.getValue();

        if (value == null) {
            line.add("<" + key + "></" + key + ">");
            return;
        }

        if (value instanceof Map) {
            Map<String, Object> v1 = (Map<String, Object>) value;
            List<String> line1 = new LinkedList<>();
            for (Map.Entry<String, Object> objectEntry : v1.entrySet()) {
                doLine(line1, objectEntry);
            }
            line.add("<" + key + ">" + Joiner.on("\r\n").join(line1) + "</" + key + ">");
            return;
        }

        if (value instanceof Collection) {
            Collection v1 = (Collection) value;
            line.add("<" + key + ">" + Joiner.on("\r\n").join(v1) + "</" + key + ">");
            return;
        }

        if (value.getClass().isArray()) {
            line.add("<" + key + ">" + Joiner.on("\r\n").join(value) + "</" + key + ">");
            return;
        }
        line.add("<" + key + ">" + value.toString() + "</" + key + ">");
    }
}
