package com.chua.xjar.support.io.xjar.jar;

import com.chua.xjar.support.io.xjar.XConstants;
import com.chua.xjar.support.io.xjar.XDecryptor;
import com.chua.xjar.support.io.xjar.XEncryptor;
import com.chua.xjar.support.io.xjar.key.XKey;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.chua.common.support.constant.CommonConstant.JAR_URL_SEPARATOR;

/**
 * 加密的URL处理器
 *
 * @author Payne 646742615@qq.com
 * 2018/11/24 13:19
 */
public class XJarURLHandler extends URLStreamHandler implements XConstants {
    private final XDecryptor xDecryptor;
    private final XEncryptor xEncryptor;
    private final XKey xKey;
    private final Set<String> indexes;

    public XJarURLHandler(XDecryptor xDecryptor, XEncryptor xEncryptor, XKey xKey, ClassLoader classLoader) throws Exception {
        this.xDecryptor = xDecryptor;
        this.xEncryptor = xEncryptor;
        this.xKey = xKey;
        this.indexes = new LinkedHashSet<>();
        Enumeration<URL> resources = classLoader.getResources(XJAR_INF_DIR + XJAR_INF_IDX);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String url = resource.toString();
            String classpath = url.substring(0, url.lastIndexOf(JAR_URL_SEPARATOR) + 2);
            InputStream in = resource.openStream();
            InputStreamReader isr = new InputStreamReader(in);
            LineNumberReader lnr = new LineNumberReader(isr);
            String name;
            while ((name = lnr.readLine()) != null) {
                indexes.add(classpath + name);
            }
        }
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        URLConnection urlConnection = new URL(url.toString()).openConnection();
        return indexes.contains(url.toString())
                && urlConnection instanceof JarURLConnection
                ? new XJarURLConnection((JarURLConnection) urlConnection, xDecryptor, xEncryptor, xKey)
                : urlConnection;
    }

}
