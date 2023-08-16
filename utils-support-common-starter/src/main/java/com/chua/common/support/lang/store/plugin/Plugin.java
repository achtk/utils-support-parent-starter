package com.chua.common.support.lang.store.plugin;

import com.chua.common.support.lang.store.StoreConfig;

import java.io.File;

/**
 * @author CH
 */
public interface Plugin {
    /**
     * 处理
     *
     * @param file        文件夹
     * @param storeConfig 配置
     */
    void doWith(File file, StoreConfig storeConfig);
}
