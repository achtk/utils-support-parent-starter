package com.chua.common.support.path;

import java.util.List;

/**
 * 条件过滤器
 *
 * @author CH
 * @since 2021-09-29
 */
public interface ConditionalFileFilter {

    /**
     * Adds the specified file filter to the list of file filters at the end of
     * the list.
     *
     * @param ioFileFilter the filter to be added
     * @since 1.1
     */
    void addFileFilter(IoFileFilter ioFileFilter);

    /**
     * Gets this conditional file filter's list of file filters.
     *
     * @return the file filter list
     * @since 1.1
     */
    List<IoFileFilter> getFileFilters();

    /**
     * Removes the specified file filter.
     *
     * @param ioFileFilter filter to be removed
     * @return {@code true} if the filter was found in the list,
     * {@code false} otherwise
     * @since 1.1
     */
    boolean removeFileFilter(IoFileFilter ioFileFilter);

    /**
     * Sets the list of file filters, replacing any previously configured
     * file filters on this filter.
     *
     * @param fileFilters the list of filters
     * @since 1.1
     */
    void setFileFilters(List<IoFileFilter> fileFilters);

}
