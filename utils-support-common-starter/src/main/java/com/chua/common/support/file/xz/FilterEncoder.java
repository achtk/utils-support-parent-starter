/*
 * FilterEncoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

interface FilterEncoder extends FilterCoder {
    /**
     * id
     *
     * @return id
     */
    long getFilterId();

    /**
     * prop
     *
     * @return prop
     */
    byte[] getFilterProps();

    /**
     * support
     *
     * @return support
     */
    boolean supportsFlushing();

    /**
     * stream
     *
     * @param out        out
     * @param arrayCache cache
     * @return stream
     */
    AbstractFinishableOutputStream getOutputStream(AbstractFinishableOutputStream out,
                                                   ArrayCache arrayCache);
}
