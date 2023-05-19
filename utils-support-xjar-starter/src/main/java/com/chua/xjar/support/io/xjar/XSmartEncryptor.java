package com.chua.xjar.support.io.xjar;

import com.chua.xjar.support.io.xjar.boot.XBootEncryptor;
import com.chua.xjar.support.io.xjar.jar.XJarEncryptor;
import com.chua.xjar.support.io.xjar.key.XKey;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 智能加密器
 *
 * @author Payne 646742615@qq.com
 * 2020/4/27 16:13
 */
public class XSmartEncryptor extends XEntryEncryptor<JarArchiveEntry> implements XEncryptor, XConstants {

    public XSmartEncryptor(XEncryptor xEncryptor) {
        super(xEncryptor);
    }

    public XSmartEncryptor(XEncryptor xEncryptor, XEntryFilter<JarArchiveEntry> filter, XEntryFilter<JarArchiveEntry> dependsFilter) {
        super(xEncryptor, filter, dependsFilter);
    }

    @Override
    public void encrypt(XKey key, File src, File dest) throws IOException {
        try (JarFile jar = new JarFile(src, false)) {
            Manifest manifest = jar.getManifest();
            Attributes attributes = manifest.getMainAttributes();
            String version = attributes.getValue("Spring-Boot-Version");
            XEncryptor encryptor = version != null ? new XBootEncryptor(xEncryptor, filter, dependsFilter) : new XJarEncryptor(xEncryptor, filter, dependsFilter);
            encryptor.encrypt(key, src, dest);
        }
    }

    @Override
    public void encrypt(XKey key, InputStream in, OutputStream out) throws IOException {
        throw new UnsupportedOperationException();
    }
}
