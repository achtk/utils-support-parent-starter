package com.chua.common.support.reflection.reflections.serializers;

import com.chua.common.support.json.Json;
import com.chua.common.support.reflection.reflections.Reflections;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * json serialization for {@link Reflections} <pre>{@code reflections.save(file, new JsonSerializer())}</pre>
 * <p></p>an example of produced json:
 * <pre>{@code
 * {
 *   "store": {
 *     "SubTypes": {
 *       "com.chua.common.support.reflections.TestModel$C1": [
 *         "com.chua.common.support.reflections.TestModel$C2",
 *         "com.chua.common.support.reflections.TestModel$C3"
 *       ]
 *     },
 *     "TypesAnnotated": {
 *       "com.chua.common.support.reflections.TestModel$AC2": [
 *         "com.chua.common.support.reflections.TestModel$C2",
 *         "com.chua.common.support.reflections.TestModel$C3"
 *       ]
 *     }
 *   }
 * }
 * }</pre>
 *
 * @author Administrator
 */
public class JsonSerializer implements Serializer {

    @Override
    public Reflections read(InputStream inputStream) {
        return Json.fromJson(new InputStreamReader(inputStream, UTF_8), Reflections.class);
    }

    @Override
    public File save(Reflections reflections, String filename) {
        File file = Serializer.prepareFile(filename);
        return file;
    }
}
