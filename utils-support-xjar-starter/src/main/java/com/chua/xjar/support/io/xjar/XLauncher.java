package com.chua.xjar.support.io.xjar;


import com.chua.xjar.support.io.xjar.key.XKey;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

/**
 * Spring-Boot 启动器
 *
 * @author Payne 646742615@qq.com
 * 2019/4/14 10:28
 */
public class XLauncher implements XConstants {
    public final String[] args;
    public final XDecryptor xDecryptor;
    public final XEncryptor xEncryptor;
    public XKey xKey;

    public XLauncher(String... args) throws Exception {
        this.args = args;
        this.xDecryptor = new XJdkDecryptor();
        this.xEncryptor = new XJdkEncryptor();

        String keypath = null;
        for (String arg : args) {
            if (arg.toLowerCase().startsWith(XJAR_KEYFILE)) {
                keypath = arg.substring(XJAR_KEYFILE.length());
            }
        }
        if (keypath != null && !"".equals(keypath.trim())) {
            readProperties(keypath);
        } else {
            scannerProperties();
        }
    }

    private void readProperties(String keypath) throws Exception {
        System.out.println("XLauncher read keyfile :" + keypath);
        Properties key = null;
        String path = XKit.absolutize(keypath);
        File file = new File(path);
        try (InputStream in = new FileInputStream(file)) {
            key = new Properties();
            key.load(in);
        }

        String algorithm = DEFAULT_ALGORITHM;
        int keysize = DEFAULT_KEYSIZE;
        int ivsize = DEFAULT_IVSIZE;
        String password = null;
        String hold = null;

        Set<String> names = key.stringPropertyNames();
        for (String name : names) {
            switch (name.toLowerCase()) {
                case XJAR_KEY_ALGORITHM:
                    algorithm = key.getProperty(name);
                    break;
                case XJAR_KEY_KEYSIZE:
                    keysize = Integer.parseInt(key.getProperty(name));
                    break;
                case XJAR_KEY_IVSIZE:
                    ivsize = Integer.parseInt(key.getProperty(name));
                    break;
                case XJAR_KEY_PASSWORD:
                    password = key.getProperty(name);
                    break;
                case XJAR_KEY_HOLD:
                    hold = key.getProperty(name);
                default:
                    break;
            }
        }
        if (password == null) {
            throw new Exception("password not in key file :" + file.getCanonicalPath());
        }

        // 不保留密钥文件
        if (hold == null || !Arrays.asList("true", "1", "yes", "y").contains(hold.trim().toLowerCase())) {
            if (file.exists() && !file.delete() && file.exists()) {
                throw new IOException("could not delete key file: " + file.getCanonicalPath());
            }
        }
        this.xKey = XKit.key(algorithm, keysize, ivsize, password);
    }

    private void scannerProperties() throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input algorithm (" + DEFAULT_ALGORITHM + ") :");
        String algorithm = scanner.nextLine();
        algorithm = algorithm == null || "".equals(algorithm.trim()) ? DEFAULT_ALGORITHM : algorithm.trim();

        System.out.println("Input key size (" + DEFAULT_KEYSIZE + ") :");
        String keySizeStr = scanner.nextLine();
        keySizeStr = keySizeStr == null || "".equals(keySizeStr.trim()) ? ("" + DEFAULT_KEYSIZE) : keySizeStr.trim();
        int keysize = Integer.parseInt(keySizeStr);

        System.out.println("Input iv size (" + DEFAULT_IVSIZE + ") :");
        String ivSizeStr = scanner.nextLine();
        ivSizeStr = ivSizeStr == null || "".equals(ivSizeStr.trim()) ? ("" + DEFAULT_IVSIZE) : ivSizeStr.trim();
        int ivsize = Integer.parseInt(ivSizeStr);

        System.out.println("Input password :");
        String password = scanner.nextLine();
        this.xKey = XKit.key(algorithm, keysize, ivsize, password);
    }

}
