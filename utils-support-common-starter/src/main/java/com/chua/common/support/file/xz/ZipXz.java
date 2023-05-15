/*
 * XZ
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.Decompress;
import com.chua.common.support.file.zip.Zip;

import java.io.*;
import java.util.zip.ZipInputStream;

/**
 * XZ constants.
 *
 * @author Administrator
 */
@Spi("zip.xz")
public class ZipXz extends Zip implements Decompress {

    @Override
    public void to(OutputStream outputStream) {
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        super.to(outputStream1);
        Xz gzip = new Xz();
        gzip.addFile("", outputStream1.toByteArray());
        gzip.to(outputStream);
    }

    @Override
    public void unFile(InputStream inputStream, File output) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new XZInputStream(inputStream))) {
            Zip.unZip(zipInputStream, output);
        }
    }
}
