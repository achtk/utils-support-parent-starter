package com.chua.common.support.file.gzip;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.zip.Zip;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * gzip
 *
 * @author CH
 */
@Spi("zip.gz")
public class ZipGzip extends Zip implements Decompress {
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
        try (ZipInputStream zipInputStream = new ZipInputStream(new GZIPInputStream(inputStream))) {
            Zip.unZip(zipInputStream, output);
        }
    }
}
