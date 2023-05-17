package com.chua.common.support.file.object;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.ObjectFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;
import com.chua.common.support.reflection.Reflect;
import com.chua.common.support.resource.ResourceConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * @author CH
 */
@Spi("json")
public class JsonFile extends AbstractResourceFile implements ObjectFile {

    public JsonFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    @Override
    public <E> E parse(Class<E> target) {
        try (InputStreamReader isr = new InputStreamReader(openInputStream(), resourceConfiguration.getCharset())) {
            return Json.fromJson(isr, target);
        } catch (IOException ignored) {
        }
        return Reflect.create(target).getObjectValue().getValue();
    }

    @Override
    public <E> List<E> parseArray(Class<E> target) {
        try (InputStreamReader isr = new InputStreamReader(openInputStream(), resourceConfiguration.getCharset())) {
            return Json.fromJson(isr, new TypeReference<List<E>>() {
            });
        } catch (IOException ignored) {
        }
        return Collections.emptyList();
    }
}
