package com.chua.common.support.lang.profile;

import com.chua.common.support.collection.PathMap;

/**
 * 配置
 *
 * @author CH
 */
public interface Profile extends PathMap {
    /**
     * 添加配置
     *
     * @param resourceUrl 配置目录
     * @return this
     */
    Profile addProfile(String resourceUrl);

    /**
     * 是否无配置
     *
     * @return 是否无配置
     */
    default boolean noConfiguration() {
        return isEmpty();
    }
}
