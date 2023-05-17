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

    /**
     * 重置
     */
    void reset();

    /**
     * 来源
     *
     * @param parent 父目录
     * @param file   文件
     * @return 结果
     */
    InnerCompressInputStream source(String parent, String file);

    /**
     * 密钥
     *
     * @param password 密钥
     * @return 结果
     */
    InnerCompressInputStream password(String password);
}
