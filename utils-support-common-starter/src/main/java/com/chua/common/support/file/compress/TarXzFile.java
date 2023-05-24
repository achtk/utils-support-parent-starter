package com.chua.common.support.file.compress;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.file.tar.TarInputStream;
import com.chua.common.support.file.tar.TarOutputStream;
import com.chua.common.support.file.xz.LZMA2Options;
import com.chua.common.support.file.xz.XZOutputStream;
import com.chua.common.support.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;

/**
 * tar.gz
 *
 * @author CH
 */
@Spi({"tar.gz"})
public class TarXzFile extends TarFile {
    public TarXzFile(ResourceFileConfiguration resourceConfiguration) {
        super(resourceConfiguration.setType("tar.gz"));
    }

    @Override
    public void pack(String folder, boolean deleteSource, String pattern) throws Exception {
        File file = toFile();
        try (TarOutputStream tarOutputStream = new TarOutputStream(new XZOutputStream(Files.newOutputStream(file.toPath()), new LZMA2Options()))) {
            File fileToZip = new File(folder);
            compressTarFile(file, fileToZip, fileToZip, "", tarOutputStream);
        } finally {
            if (isTempFile() || deleteSource) {
                FileUtils.delete(file);
            }
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new TarInputStream(new GZIPInputStream(new BufferedInputStream(super.openInputStream())));
    }
}
