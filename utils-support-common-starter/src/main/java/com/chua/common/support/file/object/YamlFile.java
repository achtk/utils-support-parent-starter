package com.chua.common.support.file.object;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ObjectFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.yaml.YamlReader;
import com.chua.common.support.reflection.Reflect;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * @author CH
 */
@Spi({"yaml", "yml"})
public class YamlFile extends AbstractResourceFile implements ObjectFile {

    public YamlFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    @Override
    public <E> E parse(Class<E> target) {
        try (YamlReader isr = new YamlReader(new InputStreamReader(openInputStream(), resourceConfiguration.getCharset()))) {
            return isr.read(target);
        } catch (IOException ignored) {
        }
        return Reflect.create(target).getObjectValue().getValue();
    }

    @Override
    public <E> List<E> parseArray(Class<E> target) {
        return Collections.singletonList(parse(target));
    }
}
