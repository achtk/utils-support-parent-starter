package com.chua.common.support.file.object;

import com.chua.common.support.file.resource.AbstractResourceFile;
import com.chua.common.support.file.resource.ObjectFile;
import com.chua.common.support.file.resource.ResourceConfiguration;
import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;
import com.chua.common.support.reflect.Reflect;
import com.chua.common.support.spi.Spi;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * @author CH
 */
@Spi("json")
public class JsonFile extends AbstractResourceFile implements ObjectFile {

    public JsonFile(ResourceConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    @Override
    public <E> E parse(Class<E> target) {
        try (InputStreamReader isr = new InputStreamReader(openInputStream(), resourceConfiguration.getCharset())) {
            return Json.fromJson(isr, target);
        } catch (IOException ignored) {
        }
        return Reflect.create(target).getObjectValue().getObject();
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
