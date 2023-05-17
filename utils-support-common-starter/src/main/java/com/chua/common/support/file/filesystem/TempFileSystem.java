package com.chua.common.support.file.filesystem;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.ResourceFile;

import java.io.InputStream;

/**
 * 临时文件
 *
 * @author CH
 */
@Spi("temp")
public class TempFileSystem extends OsFileSystem {

    public TempFileSystem(ResourceFile resourceFile) {
        super(resourceFile);
    }

    public TempFileSystem(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    <E> E transfer(Class<E> target) {
        return null;
    }
}
