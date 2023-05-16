package com.chua.common.support.lang.profile.resolver;

import com.chua.starter.core.support.annotations.Extension;
import com.chua.starter.core.support.profile.value.ProfileValue;
import com.chua.starter.core.support.profile.value.PropertiesProfileValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 解释器
 *
 * @author CH
 */
@Extension("properties")
public class PropertiesProfileResolver implements ProfileResolver {

    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        try (InputStreamReader isr = new InputStreamReader(inputStream, UTF_8)) {
            Properties properties = new Properties();
            properties.load(isr);
            return Collections.singletonList(new PropertiesProfileValue(resourceUrl, properties));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
