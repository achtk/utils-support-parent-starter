package com.chua.common.support.lang.similar;


import com.chua.common.support.lang.algorithm.EditDistanceMatchingAlgorithm;

import java.net.URL;

/**
 * 字符串
 *
 * @author CH
 * @since 2021-12-15
 */
public class StringSimilar implements Similar {
    @Override
    public Similar environment(String key, Object value) throws Exception {
        return this;
    }

    @Override
    public double match(String source, String target) throws Exception {
        return new EditDistanceMatchingAlgorithm().match(source, target);
    }

    @Override
    public double match(URL source, URL target) throws Exception {
        return -1D;
    }
}
