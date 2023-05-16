package com.chua.common.support.lang.profile.resolver;

import com.chua.starter.core.support.annotations.Extension;
import com.chua.starter.core.support.json.Json;
import com.chua.starter.core.support.profile.value.MapProfileValue;
import com.chua.starter.core.support.profile.value.ProfileValue;

import java.io.IOException;
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
@Extension("json")
public class JsonProfileResolver implements ProfileResolver {
    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        try (InputStreamReader isr = new InputStreamReader(inputStream, UTF_8)) {
            return Collections.singletonList(new MapProfileValue(resourceUrl, Json.fromJson(isr, Map.class)));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
