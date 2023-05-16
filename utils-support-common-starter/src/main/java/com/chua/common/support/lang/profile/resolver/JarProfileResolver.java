package com.chua.common.support.lang.profile.resolver;

import com.chua.common.support.annotations.Spi;

/**
 * 解释器
 *
 * @author CH
 */
@Spi({"jar"})
public class JarProfileResolver extends ZipProfileResolver {

    @Override
    protected String getSuffix() {
        return "jar";
    }
}
