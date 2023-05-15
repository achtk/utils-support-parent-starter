package com.chua.common.support.path;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * true过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public class TrueFileFilter implements IoFileFilter {
    private static final String TO_STRING = Boolean.TRUE.toString();
    /**
     * Singleton instance of true filter.
     *
     * @since 1.3
     */
    public static final IoFileFilter TRUE = new TrueFileFilter();

    /**
     * Singleton instance of true filter. Please use the identical TrueFileFilter.TRUE constant. The new name is more
     * JDK 1.5 friendly as it doesn't clash with other values when using static imports.
     */
    public static final IoFileFilter INSTANCE = TRUE;

    /**
     * Restrictive constructor.
     */
    protected TrueFileFilter() {
    }

    /**
     * Returns true.
     *
     * @param file the file to check (ignored)
     * @return true
     */
    @Override
    public boolean accept(final File file) {
        return true;
    }

    /**
     * Returns true.
     *
     * @param dir  the directory to check (ignored)
     * @param name the file name (ignored)
     * @return true
     */
    @Override
    public boolean accept(final File dir, final String name) {
        return true;
    }

    /**
     * Returns true.
     *
     * @param file the file to check (ignored)
     * @return true
     * @since 2.9.0
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public IoFileFilter negate() {
        return FalseFileFilter.INSTANCE;
    }

    @Override
    public IoFileFilter or(final IoFileFilter fileFilter) {
        // TRUE OR expression <=> true
        return INSTANCE;
    }

    @Override
    public IoFileFilter and(final IoFileFilter fileFilter) {
        // TRUE AND expression <=> expression
        return fileFilter;
    }

    @Override
    public String toString() {
        return TO_STRING;
    }
}
