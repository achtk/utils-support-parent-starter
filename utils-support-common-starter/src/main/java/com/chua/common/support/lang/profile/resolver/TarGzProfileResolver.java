package com.chua.common.support.lang.profile.resolver;

import com.chua.starter.core.support.annotations.Extension;
import com.chua.starter.core.support.file.tar.TarInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * 解释器
 *
 * @author CH
 */
@Extension({"tar.gz"})
public class TarGzProfileResolver extends TarProfileResolver {
    /**
     * 打开流
     *
     * @param inputStream 流
     * @return 流
     */
    protected TarInputStream openStream(InputStream inputStream) throws IOException {
        return new TarInputStream(new GZIPInputStream(inputStream));
    }

}
