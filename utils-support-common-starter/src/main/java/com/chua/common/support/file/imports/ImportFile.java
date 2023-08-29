package com.chua.common.support.file.imports;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 导入文件
 *
 * @author CH
 */
public interface ImportFile {
    /**
     * 导入数据
     *
     * @param inputStream 输入流
     * @param type        类型
     * @param listener    消费者
     * @param <T>         类型
     */
    <T> void imports(InputStream inputStream, Class<T> type, ImportListener<T> listener);

    /**
     * 导入数据
     *
     * @param inputStream 输入流
     * @param type        类型
     * @param <T>         类型
     * @return result
     */
    default <T> List<T> imports(InputStream inputStream, Class<T> type) {
        List<T> rs = new LinkedList<>();
        imports(inputStream, type, rs::add);

        return rs;
    }
}
