package com.chua.common.support.http.render;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.json.Json;

import java.nio.charset.StandardCharsets;

import static com.chua.common.support.http.HttpConstant.APPLICATION_JSON;

/**
 * 渲染器
 * @author CH
 */
@Spi(APPLICATION_JSON)
public class JsonRender implements Render{
    @Override
    public byte[] render(Object param, String contentType) {
        return Json.toJson(param).getBytes(StandardCharsets.UTF_8);
    }
}
