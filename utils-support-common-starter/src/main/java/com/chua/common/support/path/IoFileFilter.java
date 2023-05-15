package com.chua.common.support.path;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 文件过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public interface IoFileFilter extends FileFilter, FilenameFilter, PathFilter {

    /**
     * An empty String array.
     */
    String[] EMPTY_STRING_ARRAY = {};

    /**
     * Checks to see if the File should be accepted by this filter.
     * <p>
     * Defined in {@link FileFilter}.
     * </p>
     *
     * @param file the File to check.
     * @return true if this file matches the test.
     */
    @Override
    boolean accept(File file);

    /**
     * Checks to see if the File should be accepted by this filter.
     * <p>
     * Defined in {@link FilenameFilter}.
     * </p>
     *
     * @param dir  the directory File to check.
     * @param name the file name within the directory to check.
     * @return true if this file matches the test.
     */
    @Override
    boolean accept(File dir, String name);

    /**
     * Checks to see if the Path should be accepted by this filter.
     *
     * @param attributes 属性
     * @param path       the Path to check.
     * @return true if this path matches the test.
     * @since 2.9.0
     */
    @Override
    default FileVisitResult accept(final Path path, final BasicFileAttributes attributes) {
        return AbstractFileFilter.toFileVisitResult(accept(path.toFile()), path);
    }

    /**
     * Creates a new "and" filter with this filter.
     *
     * @param fileFilter the filter to "and".
     * @return a new filter.
     * @since 2.9.0
     */
    default IoFileFilter and(final IoFileFilter fileFilter) {
        return new AndFileFilter(this, fileFilter);
    }

    /**
     * Creates a new "not" filter with this filter.
     *
     * @return a new filter.
     * @since 2.9.0
     */
    default IoFileFilter negate() {
        return new NotFileFilter(this);
    }

    /**
     * Creates a new "or" filter with this filter.
     *
     * @param fileFilter the filter to "or".
     * @return a new filter.
     * @since 2.9.0
     */
    default IoFileFilter or(final IoFileFilter fileFilter) {
        return new OrFileFilter(this, fileFilter);
    }

}
