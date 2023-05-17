package com.chua.common.support.lang.profile.value;

import com.chua.common.support.function.ChainMap;

import java.util.Set;

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
     * 是否包含数据
     * @param name name
     * @return 结果
     */
    boolean contains(String name);

    /**
     * 添加配置
     * @param value 配置
     */
    void add(ProfileValue value);

    /**
     * keys
     * @return key
     */
    Set<String> keys();
}
