package com.chua.common.support.file.object;

import com.chua.common.support.file.resource.AbstractResourceFile;
import com.chua.common.support.file.resource.ObjectFile;
import com.chua.common.support.file.resource.ResourceConfiguration;
import com.chua.common.support.reflect.Reflect;
import com.chua.common.support.spi.Spi;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * @author CH
 */
@Spi({"yaml", "yml"})
public class YamlFile extends AbstractResourceFile implements ObjectFile {

    public YamlFile(ResourceConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    @Override
    public <E> E parse(Class<E> target) {
        try (InputStreamReader isr = new InputStreamReader(openInputStream(), resourceConfiguration.getCharset())) {
            Yaml yaml = new Yaml();
            return yaml.load(isr);
        } catch (IOException ignored) {
        }
        return Reflect.create(target).getObjectValue().getObject();
    }

    @Override
    public <E> List<E> parseArray(Class<E> target) {
        return Collections.singletonList(parse(target));
    }
}
