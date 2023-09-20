package com.chua.common.support.lang.profile.resolver;

import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.yaml.YamlReader;
import com.chua.common.support.lang.profile.value.MapProfileValue;
import com.chua.common.support.lang.profile.value.ProfileValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 解释器
 *
 * @author CH
 */
@Spi({"yaml", "yml"})
public class YamlProfileResolver implements ProfileResolver {

    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        try (YamlReader yaml = new YamlReader(new InputStreamReader(inputStream, UTF_8))) {
            return Collections.singletonList(new MapProfileValue(resourceUrl, yaml.read(JSONObject.class)));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
