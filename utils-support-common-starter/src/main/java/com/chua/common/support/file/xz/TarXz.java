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
import com.chua.common.support.file.tar.Tar;
import com.chua.common.support.file.tar.TarInputStream;

import java.io.*;

/**
 * XZ constants.
 *
 * @author Administrator
 */
@Spi("tar.xz")
public class TarXz extends Xz implements Decompress {

    @Override
    public void to(OutputStream outputStream) {
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        super.to(outputStream1);
        Xz gzip = new Xz();
        gzip.addFile("", outputStream1.toByteArray());
        gzip.to(outputStream);
    }

    @Override
    public void unFile(InputStream file, File output) throws IOException {
        try (TarInputStream tarInputStream = new TarInputStream(new XZInputStream(file))) {
            Tar.unTar(tarInputStream, output);
        }
    }
}
