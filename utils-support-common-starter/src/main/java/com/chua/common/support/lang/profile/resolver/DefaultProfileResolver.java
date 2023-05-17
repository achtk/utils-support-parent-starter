package com.chua.common.support.lang.profile.resolver;

import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.lang.profile.value.ProfileValue;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * 默认解析器
 *
 * @author CH
 */
@SpiDefault
public class DefaultProfileResolver implements ProfileResolver {
    @Override
    public List<ProfileValue> resolve(String resourceUrl, InputStream inputStream) {
        return Collections.emptyList();
    }
}
