package com.chua.common.support.lang.profile.resolver;

import com.chua.starter.core.support.annotations.Extension;

/**
 * 解释器
 *
 * @author CH
 */
@Extension({"jar"})
public class JarProfileResolver extends ZipProfileResolver {

    @Override
    protected String getSuffix() {
        return "jar";
    }
}
