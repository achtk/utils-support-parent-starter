package com.chua.common.support.extra.quickio.core;

import com.chua.common.support.extra.quickio.api.Db;
import com.chua.common.support.extra.quickio.api.Kv;
import com.chua.common.support.extra.quickio.api.Tin;

/**
 * 初始化
 * @author CH
 */
public final class Quick extends Plugin {

    public static Db using(String name) {
        return new QuDb(name);
    }


    public static Db using(Config config) {
        return new QuDb(config);
    }


    public static Kv usingKv(String name) {
        return new QuKv(name);
    }


    public static Kv usingKv(Config config) {
        return new QuKv(config);
    }


    public static Tin usingTin(String name) {
        return new QuTin(name);
    }


    public static Tin usingTin(Config config) {
        return new QuTin(config);
    }

}