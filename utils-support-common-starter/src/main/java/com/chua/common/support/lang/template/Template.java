package com.chua.common.support.lang.template;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
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
        Reader reader = new StringReader(input);

    }
}
