package com.chua.common.support.lang.template;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 模板
 *
 * @author CH
 */
public interface Template {
    /**
     * 替换模板
     *
     * @param inputStream  模板
     * @param outputStream 输出
     * @param templateData 模板数据
     */
    void resolve(InputStream inputStream, OutputStream outputStream, Map<String, Object> templateData);

    /**
     * 替换模板
     *
     * @param input        模板
     * @param outputStream 输出
     * @param templateData 模板数据
     */
    default void resolve(String input, OutputStream outputStream, Map<String, Object> templateData) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        resolve(byteArrayInputStream, outputStream, templateData);
    }
}
