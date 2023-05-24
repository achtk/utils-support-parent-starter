package com.chua.common.support.file.compress;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.ResourceFileConfiguration;

/**
 * jar文件
 *
 * @author CH
 */
@Spi({"jar"})
public class JarCompressFile extends ZipCompressFile {

    public JarCompressFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration.setType("jar"));
    }
}
