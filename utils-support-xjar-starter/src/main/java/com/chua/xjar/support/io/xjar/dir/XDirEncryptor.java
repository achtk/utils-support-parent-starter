package com.chua.xjar.support.io.xjar.dir;

import com.chua.xjar.support.io.xjar.XEncryptor;
import com.chua.xjar.support.io.xjar.XEntryEncryptor;
import com.chua.xjar.support.io.xjar.XEntryFilter;
import com.chua.xjar.support.io.xjar.key.XKey;

import java.io.File;
import java.io.IOException;

/**
 * 文件夹加密器
 *
 * @author Payne 646742615@qq.com
 * 2018/11/22 15:01
 */
public class XDirEncryptor extends XEntryEncryptor<File> implements XEncryptor {

    public XDirEncryptor(XEncryptor xEncryptor) {
        this(xEncryptor, null, null);
    }

    public XDirEncryptor(XEncryptor xEncryptor, XEntryFilter<File> filter, XEntryFilter<File> dependsFilter) {
        super(xEncryptor, filter, dependsFilter);
    }

    @Override
    public void encrypt(XKey key, File src, File dest) throws IOException {
        if (src.isFile()) {
            XEncryptor encryptor = filtrate(src) ? xEncryptor : xNopEncryptor;
            encryptor.encrypt(key, src, dest);
        } else if (src.isDirectory()) {
            File[] files = src.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                encrypt(key, files[i], new File(dest, files[i].getName()));
            }
        }
    }

}
