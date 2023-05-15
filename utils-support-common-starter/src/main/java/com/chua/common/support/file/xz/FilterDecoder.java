/*
 * FilterDecoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

import java.io.InputStream;

interface FilterDecoder extends FilterCoder {
    /**
     * 内存使用
     *
     * @return 内存使用
     */
    int getMemoryUsage();

    /**
     * stream
     *
     * @param in         in
     * @param arrayCache array
     * @return stream
     */
    InputStream getInputStream(InputStream in, ArrayCache arrayCache);
}
