package com.chua.common.support.path;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * or过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public class OrFileFilter extends AbstractFileFilter implements ConditionalFileFilter {

    /**
     * The list of file filters.
     */
    private final List<IoFileFilter> fileFilters;

    /**
     * Constructs a new instance of {@code OrFileFilter}.
     *
     * @since 1.1
     */
    public OrFileFilter() {
        this(0);
    }

    /**
     * Constructs a new instance with the given initial list.
     *
     * @param initialList the initial list.
     */
    private OrFileFilter(final ArrayList<IoFileFilter> initialList) {
        this.fileFilters = Objects.requireNonNull(initialList, "initialList");
    }

    /**
     * Constructs a new instance with the given initial capacity.
     *
     * @param initialCapacity the initial capacity.
     */
    private OrFileFilter(final int initialCapacity) {
        this(new ArrayList<>(initialCapacity));
    }

    /**
     * Constructs a new instance for the give filters.
     *
     * @param fileFilters filters to OR.
     * @since 2.9.0
     */
    public OrFileFilter(final IoFileFilter... fileFilters) {
        this(Objects.requireNonNull(fileFilters, "fileFilters").length);
        addFileFilter(fileFilters);
    }

    /**
     * Constructs a new file filter that ORs the result of other filters.
     *
     * @param filter1 the first filter, must not be null
     * @param filter2 the second filter, must not be null
     * @throws IllegalArgumentException if either filter is null
     */
    public OrFileFilter(final IoFileFilter filter1, final IoFileFilter filter2) {
        this(2);
        addFileFilter(filter1);
        addFileFilter(filter2);
    }

    /**
     * Constructs a new instance of {@code OrFileFilter} with the specified filters.
     *
     * @param fileFilters the file filters for this filter, copied.
     * @since 1.1
     */
    public OrFileFilter(final List<IoFileFilter> fileFilters) {
        this(new ArrayList<>(Objects.requireNonNull(fileFilters, "fileFilters")));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file) {
        for (final IoFileFilter fileFilter : fileFilters) {
            if (fileFilter.accept(file)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(final File file, final String name) {
        for (final IoFileFilter fileFilter : fileFilters) {
            if (fileFilter.accept(file, name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        for (final IoFileFilter fileFilter : fileFilters) {
            if (fileFilter.accept(file, attributes) == FileVisitResult.CONTINUE) {
                return FileVisitResult.CONTINUE;
            }
        }
        return FileVisitResult.TERMINATE;
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
        String symbol = "fileFilters";
        for (final IoFileFilter fileFilter : Objects.requireNonNull(fileFilters, symbol)) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFileFilter(final IoFileFilter fileFilter) {
        return this.fileFilters.remove(fileFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileFilters(final List<IoFileFilter> fileFilters) {
        this.fileFilters.clear();
        this.fileFilters.addAll(Objects.requireNonNull(fileFilters, "fileFilters"));
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
        if (fileFilters != null) {
            for (int i = 0; i < fileFilters.size(); i++) {
                if (i > 0) {
                    buffer.append(",");
                }
                buffer.append(fileFilters.get(i));
            }
        }
        buffer.append(")");
        return buffer.toString();
    }

}
