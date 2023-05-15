package com.chua.common.support.path;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 路劲过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
@FunctionalInterface
public interface PathFilter {

    /**
     * Tests whether or not to include the specified Path in a result.
     *
     * @param path       The Path to test.
     * @param attributes the file's basic attributes (TODO may be null).
     * @return a FileVisitResult
     */
    FileVisitResult accept(Path path, BasicFileAttributes attributes);
}
