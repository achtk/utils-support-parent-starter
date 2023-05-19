package com.chua.xjar.support.io.xjar.boot;

import com.chua.xjar.support.io.xjar.XLauncher;
import org.springframework.boot.loader.JarLauncher;

import java.net.URL;

/**
 * Spring-Boot Jar 启动器
 *
 * @author Payne 646742615@qq.com
 * 2018/11/23 23:06
 */
public class XJarLauncher extends JarLauncher {
    private final XLauncher xLauncher;

    public XJarLauncher(String... args) throws Exception {
        this.xLauncher = new XLauncher(args);
    }

    public static void main(String[] args) throws Exception {
        new XJarLauncher(args).launch();
    }

    public void launch() throws Exception {
        launch(xLauncher.args);
    }

    @Override
    protected ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new XBootClassLoader(urls, this.getClass().getClassLoader(), xLauncher.xDecryptor, xLauncher.xEncryptor, xLauncher.xKey);
    }

}
