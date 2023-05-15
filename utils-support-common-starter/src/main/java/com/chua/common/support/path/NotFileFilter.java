package com.chua.common.support.path;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 非过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public class NotFileFilter extends AbstractFileFilter {

    /**
     * The filter
     */
    private final IoFileFilter filter;

    /**
     * Constructs a new file filter that NOTs the result of another filter.
     *
     * @param filter the filter, must not be null
     * @throws IllegalArgumentException if the filter is null
     */
    public NotFileFilter(final IoFileFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("The filter must not be null");
        }
        this.filter = filter;
    }

    /**
     * Returns the logical NOT of the underlying filter's return value for the same File.
     *
     * @param file the File to check
     * @return true if the filter returns false
     */
    @Override
    public boolean accept(final File file) {
        return !filter.accept(file);
    }

    /**
     * Returns the logical NOT of the underlying filter's return value for the same arguments.
     *
     * @param file the File directory
     * @param name the file name
     * @return true if the filter returns false
     */
    @Override
    public boolean accept(final File file, final String name) {
        return !filter.accept(file, name);
    }

    /**
     * Returns the logical NOT of the underlying filter's return value for the same File.
     *
     * @param file the File to check
     * @return true if the filter returns false
     * @since 2.9.0
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return not(filter.accept(file, attributes));
    }

    private FileVisitResult not(final FileVisitResult accept) {
        return accept == FileVisitResult.CONTINUE ? FileVisitResult.TERMINATE
                : FileVisitResult.CONTINUE;
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        return "NOT (" + filter.toString() + ")";
    }

}
