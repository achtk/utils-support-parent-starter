package com.chua.common.support.io.inner;

import com.chua.common.support.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.chua.common.support.constant.CommonConstant.FILE_URL_PREFIX;
import static com.chua.common.support.constant.CommonConstant.HTTP;

/**
 * 简单流
 *
 * @author CH
 */
public class SimpleFileInnerCompressInputStream extends AbstractFileInnerCompressInputStream {


    @Override
    protected InputStream createStream() throws IOException {
        if(parent.endsWith(file)) {
            if(FileUtils.exist(parent)) {
                return Files.newInputStream(new File(parent).toPath());
            }

            return new URL(parent).openStream();
        }

        if (null != parent && parent.startsWith(FILE_URL_PREFIX)) {
            return Files.newInputStream(new File(parent, file).toPath());
        }

        if (null != parent && parent.startsWith(HTTP)) {
            return new URL(parent + file).openStream();
        }

        return Files.newInputStream(new File(parent, file).toPath());
    }
}
