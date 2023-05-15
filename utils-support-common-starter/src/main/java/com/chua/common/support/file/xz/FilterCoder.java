/*
 * FilterCoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

interface FilterCoder {
    /**
     * change size
     * @return is change
     */
    boolean changesSize();

    /**
     * is not ok
     * @return status
     */
    boolean nonLastOk();
    /**
     * is ok
     * @return status
     */
    boolean lastOk();
}
