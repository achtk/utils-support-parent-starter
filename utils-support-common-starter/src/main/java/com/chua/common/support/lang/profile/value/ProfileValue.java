package com.chua.common.support.lang.profile.value;

import com.chua.common.support.function.ChainMap;

/**
 * 配置值
 *
 * @author CH
 */
public interface ProfileValue extends ChainMap<ProfileValue, String, Object> {
    /**
     * 名稱
     *
     * @return 名稱
     */
    String getName();

    /**
     * 获取值
     *
     * @param key key
     * @return 结果
     */
    Object getValue(String key);

    /**
     * 获取值
     *
     * @param key key
     * @return 结果
     */
    Object getJsonValue(String key);
}
