package com.chua.xjar.support.io.xjar;

/**
 * 常量表
 *
 * @author Payne 646742615@qq.com
 * 2018/11/24 9:17
 */
public interface XConstants {
    String BOOT_INF_CLASSES = "BOOT-INF/classes/";
    String BOOT_INF_LIB = "BOOT-INF/lib/";

    String META_INF_MANIFEST = "META-INF/MANIFEST.MF";
    String XJAR_SRC_DIR = XConstants.class.getPackage().getName().replace('.', '/') + "/";
    String XJAR_INF_DIR = "XJAR-INF/";
    String XJAR_INF_IDX = "INDEXES.IDX";
    String CRLF = System.getProperty("line.separator");

    String DEFAULT_ALGORITHM = "AES/CBC/PKCS5Padding";
    int DEFAULT_KEYSIZE = 128;
    int DEFAULT_IVSIZE = 128;

    String XJAR_KEYFILE = "--xjar.keyfile=";

    String XJAR_KEY_ALGORITHM = "algorithm";
    String XJAR_KEY_KEYSIZE = "keysize";
    String XJAR_KEY_IVSIZE = "ivsize";
    String XJAR_KEY_PASSWORD = "password";
    String XJAR_KEY_HOLD = "hold";
}
