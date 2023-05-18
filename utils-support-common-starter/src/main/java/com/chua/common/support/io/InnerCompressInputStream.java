package com.chua.common.support.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 内置流
 *
 * @author CH
 */
public interface InnerCompressInputStream {


    /**
     * 获取流
     *
     * @return 流
     * @throws IOException ex
     */
    InputStream getInputStream() throws IOException;
}
