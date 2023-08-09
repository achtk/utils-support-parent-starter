package com.chua.common.support.os;

import com.chua.common.support.lang.exception.UnsupportedPlatformException;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 平台
 *
 * @author CH
 */
@Slf4j
public enum Platform {
    ;

    private static final String PRE = "library";
    static final String PRE_PREFIX = "library_openpnp";

    private static Path extractNativeBinary(final OS os, final Arch arch, String dll, String root) {
        final Set<String> location = new LinkedHashSet<>();

        switch (os) {
            case LINUX:
                dll += ".so";
                switch (arch) {
                    case X86_64:
                        location.add("/nu/pattern/" + PRE + "/linux/x86_64/" + dll);
                        location.add("/" + PRE + "/linux/x86_64/" + dll);
                        location.add("/linux/x86_64/" + dll);
                        break;
                    case ARMv7:
                        location.add("/nu/pattern/" + PRE + "/linux/ARMv7/" + dll);
                        location.add("/" + PRE + "/linux/ARMv7/" + dll);
                        location.add("/linux/ARMv7/" + dll);
                        break;
                    case ARMv8:
                        location.add("/nu/pattern/" + PRE + "/linux/ARMv8/" + dll);
                        location.add("/" + PRE + "/linux/ARMv8/" + dll);
                        location.add("/linux/ARMv8/" + dll);
                        break;
                    default:
                        throw new UnsupportedPlatformException(os, arch);
                }
                break;
            case OSX:
                dll += ".dylib";
                switch (arch) {
                    case X86_64:
                        location.add("/nu/pattern/" + PRE + "/osx/x86_64/" + dll);
                        location.add("/" + PRE + "/osx/x86_64/" + dll);
                        location.add("/osx/x86_64/" + dll);
                        break;
                    case ARMv8:
                        location.add("/nu/pattern/" + PRE + "/osx/ARMv8/" + dll);
                        location.add("/" + PRE + "/osx/ARMv8/" + dll);
                        location.add("/osx/ARMv8/" + dll);
                        break;
                    default:
                        throw new UnsupportedPlatformException(os, arch);
                }
                break;
            case WINDOWS:
                dll += ".dll";
                switch (arch) {
                    case X86_32:
                        location.add("/nu/pattern/" + PRE + "/windows/x86_32/" + dll);
                        location.add("/" + PRE + "/windows/x86_32/" + dll);
                        location.add("/windows/x86_32/" + dll);
                        break;
                    case X86_64:
                        location.add("/nu/pattern/" + PRE + "/windows/x86_64/" + dll);
                        location.add("/" + PRE + "/windows/x86_64/" + dll);
                        location.add("/windows/x86_64/" + dll);
                        break;
                    default:
                        throw new UnsupportedPlatformException(os, arch);
                }
                break;
            default:
                throw new UnsupportedPlatformException(os, arch);
        }

        if(StringUtils.isNotEmpty(root)) {
            location.addAll(FileUtils.find(root, dll));
        }
        log.info("Selected native binary \"{}\".", location);

        Path destination = null;
        for (String s : location) {

            final InputStream binary = Platform.load(s);
            if (null == binary) {
                continue;
            }

            try {
                destination = new File(s).toPath();
            } catch (Exception ignored) {
            }

            if(null == destination) {
                if (OS.WINDOWS.equals(os)) {
                    destination = new TemporaryDirectory().deleteOldInstancesOnStart().getPath().resolve("./" + s).normalize();
                } else {
                    destination = new TemporaryDirectory().markDeleteOnExit().getPath().resolve("./" + s).normalize();
                }
            }

            try {
                log.info("Copying native binary to \"{}\".", destination);
                if(Files.exists(destination)) {
                    return destination;
                }
                Files.createDirectories(destination.getParent());
                Files.copy(binary, destination);
                binary.close();
            } catch (final IOException ioe) {
                throw new IllegalStateException(String.format("Error writing native library to \"%s\".", destination), ioe);
            }

            log.info("Extracted native binary to \"{}\".", destination);
            break;
        }

        return destination;
    }

    public static InputStream load(String s) {
        try {
            URL url = UrlUtils.createUrl(s);
            return url.openStream();
        } catch (Exception ignored) {
        }

        try {
            InputStream resourceAsStream = Platform.class.getResourceAsStream(s);
            if (null != resourceAsStream) {
                return resourceAsStream;
            }
        } catch (Exception ignored) {
        }

        try {
            Resource resource = ResourceProvider.of(s).getResource();
            return resource.openStream();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析动态库地址
     *
     * @param dll 动态库名称
     * @return 地址
     */
    public static Path extractNativeBinary(String dll) {
        final OS os = OS.getCurrent();
        final Arch arch = Arch.getCurrent();
        return extractNativeBinary(os, arch, dll, "");
    }

    /**
     * 解析动态库地址
     *
     * @param dll  动态库名称
     * @param root 根目录
     * @return 地址
     */
    public static Path extractNativeBinary(String dll, String root) {
        final OS os = OS.getCurrent();
        final Arch arch = Arch.getCurrent();
        Path path = extractNativeBinary(os, arch, dll, root);
        if(null == path) {
            log.info("【未检测到】本地缓存模型");
            throw new IllegalArgumentException("【未检测到】本地缓存模型");
        }
        return path;
    }


    private static class TemporaryDirectory {
        final Path path;

        public TemporaryDirectory() {
            try {
                path = Files.createTempDirectory(PRE_PREFIX);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Path getPath() {
            return path;
        }

        public TemporaryDirectory deleteOldInstancesOnStart() {
            Path tempDirectory = path.getParent();

            for (File file : Objects.requireNonNull(tempDirectory.toFile().listFiles())) {
                if (file.isDirectory() && file.getName().startsWith(PRE_PREFIX)) {
                    try {
                        delete(file.toPath());
                    } catch (RuntimeException e) {
                        if (e.getCause() instanceof AccessDeniedException) {
                            log.error("Failed delete a previous instance of the OpenCV binaries, ");
                        }
                    }
                }
            }

            return this;
        }

        public TemporaryDirectory markDeleteOnExit() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    delete();
                }
            });

            return this;
        }

        private void delete(Path path) {
            if (!Files.exists(path)) {
                return;
            }

            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
                        Files.deleteIfExists(dir);
                        return super.postVisitDirectory(dir, e);
                    }

                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                            throws IOException {
                        Files.deleteIfExists(file);
                        return super.visitFile(file, attrs);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void delete() {
            delete(path);
        }
    }

    public static void removeLibraryPath(final Path path) {
        final String normalizedPath = path.normalize().toString();

        try {
            final Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);

            final Set<String> userPaths = new HashSet<>(Arrays.asList((String[]) field.get(null)));
            userPaths.remove(normalizedPath);

            field.set(null, userPaths.toArray(new String[0]));

            System.setProperty("java.library.path", System.getProperty("java.library.path").replace(File.pathSeparator + path.normalize().toString(), ""));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get field handle to set library path");
        }
    }

    public static void addLibraryPath(final Path path) {
        final String normalizedPath = path.normalize().toString();

        try {
            final Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);

            final Set<String> userPaths = new HashSet<>(Arrays.asList((String[]) field.get(null)));
            userPaths.add(normalizedPath);

            field.set(null, userPaths.toArray(new String[0]));

            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + normalizedPath);

            log.info("System library path now \"{}\".", System.getProperty("java.library.path"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get field handle to set library path");
        }
    }
}
