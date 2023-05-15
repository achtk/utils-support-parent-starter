package com.chua.common.support.file.gzip;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.tar.Tar;
import com.chua.common.support.file.tar.TarInputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * gzip
 *
 * @author CH
 */
@Spi({"tar.gz", "tgz"})
public class TarGzip extends Tar implements Decompress {
    @Override
    public void to(OutputStream outputStream) {
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        super.to(outputStream1);
        Gzip gzip = new Gzip();
        gzip.addFile("", outputStream1.toByteArray());
        gzip.to(outputStream);
    }

    @Override
    public void unFile(InputStream inputStream, File output) throws IOException {
        try (TarInputStream stream = new TarInputStream(new GZIPInputStream(inputStream))) {
            Tar.unTar(stream, output);
        }
    }
}
