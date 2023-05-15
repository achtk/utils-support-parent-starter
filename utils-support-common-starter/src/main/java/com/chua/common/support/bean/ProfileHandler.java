package com.chua.common.support.bean;

import java.util.Collections;
import java.util.Map;

/**
 * 配置监听器
 *
 * @author CH
 */
public interface ProfileHandler {
    /**
     * 获取值
     *
     * @param name 名称
     * @return 值
     */
    Object getProperty(String name);

    /**
     * 是否存在下级数据
     *
     * @param newKey key
     * @return 是否存在下级数据
     */
    default boolean isMatcher(String newKey) {
        return false;
    }

    /**
     * 是否存在下级数据
     *
     * @param newKey key
     * @return 是否存在下级数据
     */
    default Map<String, Object> getChildren(String newKey) {
        return Collections.emptyMap();
    }
}
