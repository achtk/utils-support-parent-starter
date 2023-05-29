package com.chua.common.support.http.render;

import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.json.Json;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.chua.common.support.http.HttpConstant.APPLICATION_JSON;

/**
 * 渲染器
 * @author CH
 */
@SpiDefault
public class SimpleRender implements Render{
    @Override
    public byte[] render(Object param, String contentType) {
        if (contentType.contains(APPLICATION_JSON)) {
            return Json.toJson(param).getBytes(StandardCharsets.UTF_8);
        }

        Map<String, Object> params = (Map) param;
        return Joiner.on("&").withKeyValueSeparator("=").join(params).getBytes(StandardCharsets.UTF_8);
    }
}
