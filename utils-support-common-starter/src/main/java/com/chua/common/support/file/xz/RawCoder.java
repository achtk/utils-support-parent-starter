/*
 * RawCoder
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

class RawCoder {
    static void validate(FilterCoder[] filters)
            throws UnsupportedOptionsException {
        for (int i = 0; i < filters.length - 1; ++i) {
            if (!filters[i].nonLastOk()) {
                throw new UnsupportedOptionsException(
                        "Unsupported XZ filter chain");
            }
        }

        if (!filters[filters.length - 1].lastOk()) {
            throw new UnsupportedOptionsException(
                    "Unsupported XZ filter chain");
        }

        int changesSizeCount = 0;
        for (int i = 0; i < filters.length; ++i) {
            if (filters[i].changesSize()) {
                ++changesSizeCount;
            }
        }

        if (changesSizeCount > 3) {
            throw new UnsupportedOptionsException(
                    "Unsupported XZ filter chain");
        }
    }
}
