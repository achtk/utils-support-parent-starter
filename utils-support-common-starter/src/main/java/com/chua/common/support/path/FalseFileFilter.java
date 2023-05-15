package com.chua.common.support.path;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * false过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public class FalseFileFilter implements IoFileFilter {

    private static final String TO_STRING = Boolean.FALSE.toString();

    /**
     * Singleton instance of false filter.
     *
     * @since 1.3
     */
    public static final IoFileFilter FALSE = new FalseFileFilter();

    /**
     * Singleton instance of false filter. Please use the identical FalseFileFilter.FALSE constant. The new name is more
     * JDK 1.5 friendly as it doesn't clash with other values when using static imports.
     */
    public static final IoFileFilter INSTANCE = FALSE;

    private static final long serialVersionUID = 6210271677940926200L;

    /**
     * Restrictive constructor.
     */
    protected FalseFileFilter() {
    }

    /**
     * Returns false.
     *
     * @param file the file to check (ignored)
     * @return false
     */
    @Override
    public boolean accept(final File file) {
        return false;
    }

    /**
     * Returns false.
     *
     * @param dir  the directory to check (ignored)
     * @param name the file name (ignored)
     * @return false
     */
    @Override
    public boolean accept(final File dir, final String name) {
        return false;
    }

    /**
     * Returns false.
     *
     * @param file the file to check (ignored)
     * @return false
     * @since 2.9.0
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return FileVisitResult.TERMINATE;
    }

    @Override
    public IoFileFilter negate() {
        return TrueFileFilter.INSTANCE;
    }

    @Override
    public String toString() {
        return TO_STRING;
    }

    @Override
    public IoFileFilter and(final IoFileFilter fileFilter) {
        // FALSE AND expression <=> FALSE
        return INSTANCE;
    }

    @Override
    public IoFileFilter or(final IoFileFilter fileFilter) {
        // FALSE OR expression <=> expression
        return fileFilter;
    }
}
