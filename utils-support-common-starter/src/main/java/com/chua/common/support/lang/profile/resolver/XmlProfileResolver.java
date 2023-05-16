package com.chua.common.support.lang.profile.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.xml.XML;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.JsonObject;
import com.chua.common.support.lang.profile.value.MapProfileValue;
import com.chua.common.support.lang.profile.value.ProfileValue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 解释器
 *
 * @author CH
 */
@Spi("xml")
public class XmlProfileResolver implements ProfileResolver {

    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        try (InputStreamReader isr = new InputStreamReader(inputStream, UTF_8)) {
            JsonObject jsonObject = XML.toJsonObject(isr);
            String string = jsonObject.toString();
            return Collections.singletonList(new MapProfileValue(resourceUrl, Json.fromJson(string, Map.class)));
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }
}
