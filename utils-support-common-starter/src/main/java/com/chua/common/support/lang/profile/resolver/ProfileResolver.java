package com.chua.common.support.lang.profile.resolver;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.profile.value.ProfileValue;
import com.chua.common.support.utils.UrlUtils;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * 解释器
 *
 * @author CH
 */
public interface ProfileResolver {

    /**
     * 解析配置
     *
     * @param resourceUrl 资源文件
     * @return profile
     */
    default List<ProfileValue> resolve(String resourceUrl) {
        try {
            return resolve(resourceUrl, Converter.convertIfNecessary(resourceUrl, InputStream.class));
        } catch (Throwable e) {
            return Collections.emptyList();
        }
    }

    /**
     * 解析配置
     *
     * @param resourceUrl 资源文件
     * @param inputStream 流
     * @return profile
     */
    List<ProfileValue> resolve(String resourceUrl, InputStream inputStream);
}
