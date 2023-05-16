package com.chua.common.support.lang.profile.resolver;

import com.chua.starter.core.support.profile.value.ProfileValue;
import com.chua.starter.core.support.utils.UrlUtils;

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
            return resolve(resourceUrl, UrlUtils.createUrl(resourceUrl).openStream());
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
