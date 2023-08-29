package com.chua.common.support.lang.spider.utils;

import java.util.*;

/**
 * @author code4crafter@gmail.com
 * Date: 16/12/18
 * Time: 上午10:16
 */
public class WmCollections {

    public static <T> Set<T> newHashSet(T... t) {
        Set<T> set = new HashSet<T>(t.length);
        set.addAll(Arrays.asList(t));
        return set;
    }

    public static <T> List<T> newArrayList(T... t) {
        List<T> set = new ArrayList<T>(t.length);
        set.addAll(Arrays.asList(t));
        return set;
    }
}
