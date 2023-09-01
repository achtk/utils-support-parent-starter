package com.chua.common.support.objects.environment.resolver;

import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.YamlReader;
import com.chua.common.support.objects.environment.properties.PropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * yml处理器
 * @author CH
 */
public class YmlPropertiesSourceResolver extends AbstractPropertySourceResolver{


    public YmlPropertiesSourceResolver(String name, InputStream inputStream) {
        super(name, inputStream);
    }

    @Override
    public PropertySource get() {
        YamlConfig yamlConfig = new YamlConfig();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            YamlReader yamlReader = new YamlReader(reader);
        } catch (IOException ignored) {
        }
        return null;
    }
}
