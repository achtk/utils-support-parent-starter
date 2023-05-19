package com.chua.xjar.support.io.loadkit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * 资源加载器工具类
 *
 * @author Payne 646742615@qq.com
 * 2018/12/2 16:21
 */
public abstract class Loaders {

    /**
     * 创建 classpath 资源加载器，也就是对{@link ClassLoader#getResources(String)}的增强封装。
     * 默认采用 {@link Thread#currentThread()}的{@link Thread#getContextClassLoader()}，作为ClassLoader,
     * 如果当前线程的上下文类加载器为{@code null} 则采用{@link ClassLoader#getSystemClassLoader()}.
     * 示例：
     * <p>1. Loaders.std().load("io/loadkit"); 加载classpath中"io/loadkit"目录下的所有资源，但不包括子目录。</p>
     * <p>2. Loaders.std().load("io/", true); 加载classpath中"io/"目录下的所有资源，而且包括子目录。</p>
     *
     * @return classpath 资源加载器
     */
    public static Loader std() {
        return new StdLoader();
    }

    /**
     * 创建 classpath 资源加载器，并且指定{@link ClassLoader}
     * 示例：
     * <p>1. Loaders.std().load("io/loadkit"); 加载classpath中"io/loadkit"目录下的所有资源，但不包括子目录。</p>
     * <p>2. Loaders.std().load("io/", true); 加载classpath中"io/"目录下的所有资源，而且包括子目录。</p>
     *
     * @return classpath 资源加载器
     */
    public static Loader std(ClassLoader classLoader) {
        return new StdLoader(classLoader);
    }

    /**
     * 创建按包名来加载的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给{@link Loaders#std()}资源加载器。
     * 示例：在{@link Loaders#std()}创建的资源加载器中加载类资源路径表达实际上不太直观，往往采用包名的方式更清晰易懂
     * <p>1. Loaders.pkg().load("io.loadkit"); 加载classpath中"io.loadkit"包下的所有资源，但不包括子包。</p>
     * <p>2. Loaders.pkg().load("io", true); 加载classpath中"io"包下的所有资源，而且包括子子包。</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader pkg() {
        return new PkgLoader();
    }

    /**
     * 创建按包名来加载的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给{@link Loaders#std(ClassLoader)}资源加载器。
     * 示例：在{@link Loaders#std()}创建的资源加载器中加载类资源路径表达实际上不太直观，往往采用包名的方式更清晰易懂
     * <p>1. Loaders.pkg().load("io.loadkit"); 加载classpath中"io.loadkit"包下的所有资源，但不包括子包。</p>
     * <p>2. Loaders.pkg().load("io", true); 加载classpath中"io"包下的所有资源，而且包括子子包。</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader pkg(ClassLoader classLoader) {
        return new PkgLoader(classLoader);
    }

    /**
     * 创建按包名来加载的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给delegate资源加载器。
     * 通过包装一个实际资源加载器可以实现更灵活的加载方式，例如：
     * <p>1. Loaders.pkg(Loaders.ant()).load("io.loadkit.*"); 加载io.loadkit包下的资源，但不递归加载子包。</p>
     * <p>2. Loaders.pkg(Loaders.ant()).load("io.**"); 加载io包以及子包的资源，而且递归加载任意层次的子包。</p>
     * <p>3. Loaders.pkg(Loaders.ant()).load("io.load???.*"); 加载io包下以load开头并且跟着三个字符的子包的所有资源。</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader pkg(Loader delegate) {
        return new PkgLoader(delegate);
    }

    /**
     * 创建ANT风格路径表达式的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给{@link Loaders#std()}资源加载器。
     * 示例：
     * <p>1. Loaders.ant().load("io/loadkit/*"); 加载io/loadkit/目录下的资源，但不包括子目录。</p>
     * <p>2. Loaders.ant().load("io/**"); 加载io/目录下的资源以及递归加载所有子目录的资源。</p>
     * <p>3. Loaders.ant().load("io/loadkit/*Loader.class"); 加载io/loadkit/目录下以Loader.class结尾的资源</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader ant() {
        return new AntLoader();
    }

    /**
     * 创建ANT风格路径表达式的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给{@link Loaders#std(ClassLoader)}资源加载器。
     * 示例：
     * <p>1. Loaders.ant().load("io/loadkit/*"); 加载io/loadkit/目录下的资源，但不包括子目录。</p>
     * <p>2. Loaders.ant().load("io/**"); 加载io/目录下的资源以及递归加载所有子目录的资源。</p>
     * <p>3. Loaders.ant().load("io/loadkit/*Loader.class"); 加载io/loadkit/目录下以Loader.class结尾的资源</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader ant(ClassLoader classLoader) {
        return new AntLoader(classLoader);
    }

    /**
     * 创建ANT风格路径表达式的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给delegate资源加载器。
     * 示例：
     * <p>1. Loaders.ant().load("io/loadkit/*"); 加载io/loadkit/目录下的资源，但不包括子目录。</p>
     * <p>2. Loaders.ant().load("io/**"); 加载io/目录下的资源以及递归加载所有子目录的资源。</p>
     * <p>3. Loaders.ant().load("io/loadkit/*Loader.class"); 加载io/loadkit/目录下以Loader.class结尾的资源</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader ant(Loader delegate) {
        return new AntLoader(delegate);
    }

    /**
     * 创建正则表达式的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给{@link Loaders#std()}资源加载器。
     * 示例：
     * <p>1. Loaders.regex().load("io/loadkit/\\w+Loader.class"); 加载io/loadkit/目录下以Loader.class结尾的资源。</p>
     * <p>2. Loaders.regex().load("io/.*"); 加载io包下所有资源。</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader regex() {
        return new RegexLoader();
    }

    /**
     * 创建正则表达式的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给{@link Loaders#std(ClassLoader)}资源加载器。
     * 示例：
     * <p>1. Loaders.regex().load("io/loadkit/\\w+Loader.class"); 加载io/loadkit/目录下以Loader.class结尾的资源。</p>
     * <p>2. Loaders.regex().load("io/.*"); 加载io包下所有资源。</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader regex(ClassLoader classLoader) {
        return new RegexLoader(classLoader);
    }

    /**
     * 创建正则表达式的资源加载器，这是一个委派加载器，自身并没有资源加载逻辑而是委派给实际的资源加载器，
     * 在这个创建方法中，实际委派给delegate资源加载器。
     * 示例：
     * <p>1. Loaders.regex().load("io/loadkit/\\w+Loader.class"); 加载io/loadkit/目录下以Loader.class结尾的资源。</p>
     * <p>2. Loaders.regex().load("io/.*"); 加载io包下所有资源。</p>
     *
     * @return 按包名来加载的资源加载器
     */
    public static Loader regex(Loader delegate) {
        return new RegexLoader(delegate);
    }

    /**
     * 创建文件资源加载器
     *
     * @param root 根目录
     * @return 文件资源加载器
     * @throws IOException I/O 异常
     */
    public static Loader file(File root) throws IOException {
        return new FileLoader(root);
    }

    /**
     * 创建文件资源加载器
     *
     * @param fileURL 文件根目录URL地址
     * @return 文件资源加载器
     */
    public static Loader file(URL fileURL) {
        return new FileLoader(fileURL);
    }

    /**
     * 创建文件资源加载器
     *
     * @param context 文件根目录URL上下文
     * @param root    根目录
     * @return 文件资源加载器
     */
    public static Loader file(URL context, File root) {
        return new FileLoader(context, root);
    }

    /**
     * 创建JAR包资源加载器
     *
     * @param file JAR包文件
     * @return AR包资源加载器
     * @throws IOException I/O 异常
     */
    public static Loader jar(File file) throws IOException {
        return new JarLoader(file);
    }

    /**
     * 创建JAR包资源加载器
     *
     * @param jarURL JAR包URL地址
     * @return 文件资源加载器
     */
    public static Loader jar(URL jarURL) throws IOException {
        return new JarLoader(jarURL);
    }

    /**
     * 创建JAR包资源加载器
     *
     * @param context JAR包URL上下文
     * @param jarFile JAR文件
     * @return JAR包资源加载器
     */
    public static Loader jar(URL context, JarFile jarFile) {
        return new JarLoader(context, jarFile);
    }
}
