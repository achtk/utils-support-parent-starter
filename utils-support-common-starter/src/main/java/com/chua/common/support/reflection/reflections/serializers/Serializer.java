package com.chua.common.support.reflection.reflections.serializers;

import com.chua.common.support.reflection.reflections.Reflections;

import java.io.File;
import java.io.InputStream;

/**
 * de/serialization for {@link Reflections} instance metadata
 * <p>see {@link XmlSerializer}, {@link JsonSerializer}, {@link JavaCodeSerializer}
 *
 * @author Administrator
 */
public interface Serializer {
    /**
     * reads the input stream into a new Reflections instance, populating it's store
     *
     * @param inputStream is
     * @return ref
     */
    Reflections read(InputStream inputStream);

    /**
     * saves a Reflections instance into the given filename
     *
     * @param reflections ref
     * @param filename    filename
     * @return file
     */
    File save(Reflections reflections, String filename);

    /**
     * 预处理
     *
     * @param filename file name
     * @return file
     */

    static File prepareFile(String filename) {
        File file = new File(filename);
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parent.mkdirs();
        }
        return file;
    }
}
