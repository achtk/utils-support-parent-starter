package com.chua.common.support.file.xz.simple;

/**
 * @author Administrator
 */
public interface SimpleFilter {
    /**
     * code
     *
     * @param buf buf
     * @param off offset
     * @param len length
     * @return int
     */
    int code(byte[] buf, int off, int len);
}
