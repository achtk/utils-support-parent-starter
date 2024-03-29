package com.chua.common.support.reflection.reflections.vfs;

import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.ReflectionsException;
import com.chua.common.support.reflection.reflections.util.AbstractClasspathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * a simple virtual file system bridge
 * <p>use the {@link BaseVfs#fromUrl(URL)} to get a {@link Dir},
 * then use {@link Dir#getFiles()} to iterate over the {@link VfsFile}
 * <p>for example:
 * <pre>
 *      Vfs.Dir dir = Vfs.fromURL(url);
 *      Iterable<Vfs.File> files = dir.getFiles();
 *      for (Vfs.File file : files) {
 *          InputStream is = file.openInputStream();
 *      }
 * </pre>
 * <p>{@link BaseVfs#fromUrl(URL)} uses static {@link DefaultUrlTypes} to resolve URLs.
 * It contains VfsTypes for handling for common resources such as local jar file, local directory, jar url, jar input stream and more.
 * <p>It can be plugged in with other {@link UrlType} using {@link BaseVfs#addDefaultUrlTypes(UrlType)} or {@link BaseVfs#setDefaultUrlTypes(List)}.
 * <p>for example:
 * <pre>
 *      Vfs.addDefaultURLTypes(new Vfs.UrlType() {
 *          public boolean matches(URL url)         {
 *              return url.getProtocol().equals("http");
 *          }
 *          public Vfs.Dir createDir(final URL url) {
 *              return new HttpDir(url); //implement this type... (check out a naive implementation on VfsTest)
 *          }
 *      });
 *
 *      Vfs.Dir dir = Vfs.fromURL(new URL("http://mirrors.ibiblio.org/pub/mirrors/maven2/org/slf4j/slf4j-api/1.5.6/slf4j-api-1.5.6.jar"));
 * </pre>
 * <p>use {@link BaseVfs#findFiles(Collection, Predicate)} to get an
 * iteration of files matching given name predicate over given list of urls
 *
 * @author Administrator
 */
public abstract class BaseVfs {
    private static List<UrlType> defaultUrlTypes = new ArrayList<>(Arrays.asList(DefaultUrlTypes.values()));

    /**
     * an abstract vfs dir
     */
    public interface Dir {
        /**
         * 获取路径
         *
         * @return {@link String}
         */
        String getPath();

        /**
         * 得到文件
         *
         * @return {@link Iterable}<{@link VfsFile}>
         */
        Iterable<VfsFile> getFiles();

        /**
         * 关闭
         */
        default void close() {
        }
    }

    /**
     * an abstract vfs file
     */
    public interface VfsFile {
        /**
         * 得到名字
         *
         * @return {@link String}
         */
        String getName();

        /**
         * 得到相对路径
         *
         * @return {@link String}
         */
        String getRelativePath();

        /**
         * 打开输入流
         *
         * @return {@link InputStream}
         * @throws IOException ioexception
         */
        InputStream openInputStream() throws IOException;
    }

    /**
     * a matcher and factory for a url
     */
    public interface UrlType {
        /**
         * 匹配
         *
         * @param url url
         * @return boolean
         * @throws Exception 异常
         */
        boolean matches(URL url) throws Exception;

        /**
         * 创建dir
         *
         * @param url url
         * @return {@link Dir}
         * @throws Exception 异常
         */
        Dir createDir(URL url) throws Exception;
    }

    /**
     * the default url types that will be used when issuing {@link BaseVfs#fromUrl(URL)}
     */
    public static List<UrlType> getDefaultUrlTypes() {
        return defaultUrlTypes;
    }

    /**
     * sets the static default url types. can be used to statically plug in urlTypes
     */
    public static void setDefaultUrlTypes(final List<UrlType> urlTypes) {
        defaultUrlTypes = urlTypes;
    }

    /**
     * add a static default url types to the beginning of the default url types list. can be used to statically plug in urlTypes
     */
    public static void addDefaultUrlTypes(UrlType urlType) {
        defaultUrlTypes.add(0, urlType);
    }

    /**
     * tries to create a Dir from the given url, using the defaultUrlTypes
     */
    public static Dir fromUrl(final URL url) {
        return fromUrl(url, defaultUrlTypes);
    }

    /**
     * tries to create a Dir from the given url, using the given urlTypes
     */
    public static Dir fromUrl(final URL url, final List<UrlType> urlTypes) {
        for (UrlType type : urlTypes) {
            try {
                if (type.matches(url)) {
                    Dir dir = type.createDir(url);
                    if (dir != null) {
                        return dir;
                    }
                }
            } catch (Throwable e) {
                if (Reflections.log != null) {
                    Reflections.log.warn("could not create Dir using " + type + " from url " + url.toExternalForm() + ". skipping.", e);
                }
            }
        }

        throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\n" +
                "either use fromURL(final URL url, final List<UrlType> urlTypes) or " +
                "use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) " +
                "with your specialized UrlType.");
    }

    /**
     * tries to create a Dir from the given url, using the given urlTypes
     */
    public static Dir fromUrl(final URL url, final UrlType... urlTypes) {
        return fromUrl(url, Arrays.asList(urlTypes));
    }

    /**
     * return an iterable of all {@link VfsFile} in given urls, starting with given packagePrefix and matching nameFilter
     */
    public static Iterable<VfsFile> findFiles(final Collection<URL> inUrls, final String packagePrefix, final Predicate<String> nameFilter) {
        Predicate<VfsFile> fileNamePredicate = file -> {
            String path = file.getRelativePath();
            if (path.startsWith(packagePrefix)) {
                String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
                return !filename.isEmpty() && nameFilter.test(filename.substring(1));
            } else {
                return false;
            }
        };
        return findFiles(inUrls, fileNamePredicate);
    }

    /**
     * return an iterable of all {@link VfsFile} in given urls, matching filePredicate
     */
    public static Iterable<VfsFile> findFiles(final Collection<URL> urls, final Predicate<VfsFile> filePredicate) {
        return () -> urls.stream()
                .flatMap(url -> {
                    try {
                        return StreamSupport.stream(fromUrl(url).getFiles().spliterator(), false);
                    } catch (Throwable e) {
                        if (Reflections.log != null) {
                            Reflections.log.error("could not findFiles for url. continuing. [" + url + "]", e);
                        }
                        return Stream.of();
                    }
                }).filter(filePredicate).iterator();
    }

    /**
     * try to get {@link java.io.File} from url
     */
    public static java.io.File getFile(URL url) {
        java.io.File file;
        String path;

        try {
            path = url.toURI().getSchemeSpecificPart();
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }
        } catch (URISyntaxException ignored) {
        }

        try {
            path = URLDecoder.decode(url.getPath(), "UTF-8");
            if (path.contains(JAR_FILE_EXTENSION_IN)) {
                path = path.substring(0, path.lastIndexOf(JAR_FILE_EXTENSION_IN) + ".jar".length());
            }
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }

        } catch (UnsupportedEncodingException ignored) {
        }

        try {
            path = url.toExternalForm();
            if (path.startsWith(JAR_URL_PREFIX)) {
                path = path.substring("jar:".length());
            }
            if (path.startsWith(WS_JAR_URL_PREFIX)) {
                path = path.substring("wsjar:".length());
            }
            if (path.startsWith(FILE_URL_PREFIX)) {
                path = path.substring("file:".length());
            }
            if (path.contains(JAR_FILE_EXTENSION_IN)) {
                path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
            }
            if (path.contains(WAR_FILE_EXTENSION_IN)) {
                path = path.substring(0, path.indexOf(".war!") + ".war".length());
            }
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }

            path = path.replace("%20", " ");
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private static boolean hasJarFileInPath(URL url) {
        return url.toExternalForm().matches(".*\\.jar(!.*|$)");
    }

    private static boolean hasInnerJarFileInPath(URL url) {
        return url.toExternalForm().matches(".+\\.jar!/.+");
    }

    /**
     * default url types used by {@link BaseVfs#fromUrl(URL)}
     * <p>
     * <p>jarFile - creates a {@link ZipDir} over jar file
     * <p>jarUrl - creates a {@link ZipDir} over a jar url, using Java's {@link JarURLConnection}
     * <p>directory - creates a {@link SystemDir} over a file system directory
     * <p>jboss vfs - for protocols vfs, using jboss vfs (should be provided in classpath)
     * <p>jboss vfsfile - creates a {@link UrlTypeVfs} for protocols vfszip and vfsfile.
     * <p>bundle - for bundle protocol, using eclipse FileLocator (should be provided in classpath)
     * <p>jarInputStream - creates a {@link JarInputDir} over jar files (contains ".jar!/" in it's name), using Java's JarInputStream
     */
    public enum DefaultUrlTypes implements UrlType {
        /**
         * jar
         */
        jarFile {
            @Override
            public boolean matches(URL url) {
                return "file".equals(url.getProtocol()) && hasJarFileInPath(url);
            }

            @Override
            public Dir createDir(final URL url) throws Exception {
                return new ZipDir(new JarFile(getFile(url)));
            }
        },
        /**
         * jar
         */
        jarUrl {
            @Override
            public boolean matches(URL url) {
                return ("jar".equals(url.getProtocol()) || "zip".equals(url.getProtocol()) || "wsjar".equals(url.getProtocol())) && !hasInnerJarFileInPath(url);
            }

            @Override
            public Dir createDir(URL url) throws Exception {
                try {
                    URLConnection urlConnection = url.openConnection();
                    if (urlConnection instanceof JarURLConnection) {
                        urlConnection.setUseCaches(false);
                        return new ZipDir(((JarURLConnection) urlConnection).getJarFile());
                    }
                } catch (Throwable e) { /*fallback*/ }
                java.io.File file = getFile(url);
                if (file != null) {
                    return new ZipDir(new JarFile(file));
                }
                return null;
            }
        },
        /**
         * directory
         */
        directory {
            @Override
            public boolean matches(URL url) {
                if (URL_PROTOCOL_FILE.equals(url.getProtocol()) && !hasJarFileInPath(url)) {
                    java.io.File file = getFile(url);
                    return file != null && file.isDirectory();
                } else {
                    return false;
                }
            }

            @Override
            public Dir createDir(final URL url) throws Exception {
                return new SystemDir(getFile(url));
            }
        },

        /**
         * jboss_vfsfile
         */
        jboss_vfsfile {
            @Override
            public boolean matches(URL url) throws Exception {
                return "vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol());
            }

            @Override
            public Dir createDir(URL url) throws Exception {
                return new UrlTypeVfs().createDir(url);
            }
        },
        /**
         * bundle
         */
        bundle {
            @Override
            public boolean matches(URL url) throws Exception {
                return url.getProtocol().startsWith("bundle");
            }

            @Override
            public Dir createDir(URL url) throws Exception {
                return fromUrl((URL) AbstractClasspathHelper.contextClassLoader().
                        loadClass("org.eclipse.core.runtime.FileLocator").getMethod("resolve", URL.class).invoke(null, url));
            }
        },
        /**
         * jarInputStream
         */
        jarInputStream {
            @Override
            public boolean matches(URL url) throws Exception {
                return url.toExternalForm().contains(".jar");
            }

            @Override
            public Dir createDir(final URL url) throws Exception {
                return new JarInputDir(url);
            }
        }
    }
}
