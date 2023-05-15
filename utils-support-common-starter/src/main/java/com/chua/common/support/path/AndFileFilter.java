package com.chua.common.support.path;

import com.chua.common.support.constant.CommonConstant;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * and过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public class AndFileFilter extends AbstractFileFilter
        implements ConditionalFileFilter {

    /**
     * The list of file filters.
     */
    private final List<IoFileFilter> fileFilters;

    /**
     * Constructs a new empty instance.
     *
     * @since 1.1
     */
    public AndFileFilter() {
        this(0);
    }

    /**
     * Constructs a new instance with the given initial list.
     *
     * @param initialList the initial list.
     */
    private AndFileFilter(final ArrayList<IoFileFilter> initialList) {
        this.fileFilters = Objects.requireNonNull(initialList, "initialList");
    }

    /**
     * Constructs a new instance with the given initial capacity.
     *
     * @param initialCapacity the initial capacity.
     */
    private AndFileFilter(final int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    /**
     * Constructs a new file filter that ANDs the result of other filters.
     *
     * @param filter1 the first filter, must second be null
     * @param filter2 the first filter, must not be null
     * @throws IllegalArgumentException if either filter is null
     */
    public AndFileFilter(final IoFileFilter filter1, final IoFileFilter filter2) {
        this(2);
        addFileFilter(filter1);
        addFileFilter(filter2);
    }

    /**
     * Constructs a new instance for the give filters.
     *
     * @param fileFilters filters to OR.
     * @since 2.9.0
     */
    public AndFileFilter(final IoFileFilter... fileFilters) {
        this(Objects.requireNonNull(fileFilters, "fileFilters").length);
        addFileFilter(fileFilters);
    }

    /**
     * Constructs a new instance of {@code AndFileFilter}
     * with the specified list of filters.
     *
     * @param fileFilters a List of IOFileFilter instances, copied.
     * @since 1.1
     */
    public AndFileFilter(final List<IoFileFilter> fileFilters) {
        this(new ArrayList<>(Objects.requireNonNull(fileFilters, "fileFilters")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file) {
        if (isEmpty()) {
            return false;
        }
        for (final IoFileFilter fileFilter : fileFilters) {
            if (!fileFilter.accept(file)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file, final String name) {
        if (isEmpty()) {
            return false;
        }
        for (final IoFileFilter fileFilter : fileFilters) {
            if (!fileFilter.accept(file, name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.9.0
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        if (isEmpty()) {
            return FileVisitResult.TERMINATE;
        }
        for (final IoFileFilter fileFilter : fileFilters) {
            if (fileFilter.accept(file, attributes) != FileVisitResult.CONTINUE) {
                return FileVisitResult.TERMINATE;
            }
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileFilter(final IoFileFilter fileFilter) {
        this.fileFilters.add(Objects.requireNonNull(fileFilter, "fileFilter"));
    }

    /**
     * Adds the given file filters.
     *
     * @param fileFilters the filters to add.
     * @since 2.9.0
     */
    public void addFileFilter(final IoFileFilter... fileFilters) {
        for (final IoFileFilter fileFilter : Objects.requireNonNull(fileFilters, CommonConstant.FILE_FILTERS)) {
            addFileFilter(fileFilter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IoFileFilter> getFileFilters() {
        return Collections.unmodifiableList(this.fileFilters);
    }

    private boolean isEmpty() {
        return this.fileFilters.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFileFilter(final IoFileFilter ioFileFilter) {
        return this.fileFilters.remove(ioFileFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileFilters(final List<IoFileFilter> fileFilters) {
        this.fileFilters.clear();
        this.fileFilters.addAll(fileFilters);
    }

    /**
     * Provide a String representation of this file filter.
     *
     * @return a String representation
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(super.toString());
        buffer.append("(");
        for (int i = 0; i < fileFilters.size(); i++) {
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append(fileFilters.get(i));
        }
        buffer.append(")");
        return buffer.toString();
    }

}
