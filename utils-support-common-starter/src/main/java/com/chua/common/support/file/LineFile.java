package com.chua.common.support.file;

import java.io.IOException;
import java.util.function.Function;

/**
 * 行处理
 *
 * @author CH
 */
public interface LineFile<Line> {
    /**
     * 行数据
     *
     * @param line 行数据
     * @param skip 跳过几行
     * @throws IOException ex
     */
    void line(Function<Line, Boolean> line, int skip) throws IOException;

    /**
     * 行数据
     *
     * @param line 行数据
     * @throws IOException ex
     */
    default void line(Function<Line, Boolean> line) throws IOException {
        line(line, 0);
    }
}
