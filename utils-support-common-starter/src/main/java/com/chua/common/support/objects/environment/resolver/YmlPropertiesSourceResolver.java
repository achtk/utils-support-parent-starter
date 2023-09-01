package com.chua.common.support.objects.environment.resolver;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.LevelsClose;
import com.chua.common.support.file.yaml.YamlConfig;
import com.chua.common.support.file.yaml.YamlReader;
import com.chua.common.support.objects.environment.properties.MapPropertySource;
import com.chua.common.support.objects.environment.properties.PropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * yml处理器
 * @author CH
 */
@Spi({"yml", "yaml"})
public class YmlPropertiesSourceResolver extends AbstractPropertySourceResolver{


    public YmlPropertiesSourceResolver(String name, InputStream inputStream) {
        super(name, inputStream);
    }

    @Override
    @SuppressWarnings("ALL")
    public PropertySource get() {
        YamlConfig yamlConfig = new YamlConfig();
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             YamlReader yamlReader = new YamlReader(reader, yamlConfig);
        ) {
            HashMap read = yamlReader.read(HashMap.class);
            Map<String, Object> value = new LinkedHashMap<>(read);
            value.putAll(new LevelsClose().apply(read));
            return new MapPropertySource(name, value);
        } catch (IOException ignored) {
        }
        return null;
    }
}
