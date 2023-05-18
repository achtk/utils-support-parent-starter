package com.alibaba.json.util;

public interface Function<ARG, V> {
    /**
     * Computes a result
     *
     * @return computed result
     */
    V apply(ARG arg);
}
