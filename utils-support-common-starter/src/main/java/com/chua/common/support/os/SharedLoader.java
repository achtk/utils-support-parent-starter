package com.chua.common.support.os;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;

/**
 * 动态链接库加载器
 *
 * @author CH
 */
@Slf4j
public class SharedLoader {

    private Path libraryPath;

    private SharedLoader(String nativeLibraryName) {
        for (String s : nativeLibraryName.split(SYMBOL_COMMA)) {
            try {
                System.loadLibrary(nativeLibraryName);
                log.info("Loaded existing OpenCV library \"{}\" from library path.", nativeLibraryName);
            } catch (final UnsatisfiedLinkError ule) {

                if (Double.parseDouble(System.getProperty("java.specification.version")) >= 12) {
                    log.warn("loadShared() is not supported in Java >= 12. Falling back to loadLocally().");
                    loadLocally(nativeLibraryName);
                    return;
                }

                /* Retain this path for cleaning up the library path later. */
                this.libraryPath = Platform.extractNativeBinary(nativeLibraryName);

                addLibraryPath(libraryPath.getParent());
                System.loadLibrary(nativeLibraryName);

                log.info("library \"{}\" loaded from extracted copy at \"{}\".", nativeLibraryName, System.mapLibraryName(nativeLibraryName));
            }
        }
    }

    public static void load(String nativeLibraryName) {
        getInstance(nativeLibraryName);
    }

    public void loadLocally(String nativeLibraryName) {
        final Path libraryPath = Platform.extractNativeBinary(nativeLibraryName);
        System.load(libraryPath.normalize().toString());
    }
    /**
     * Cleans up patches done to the environment.
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (null == libraryPath) {
            return;
        }

        removeLibraryPath(libraryPath.getParent());
    }

    public static SharedLoader getInstance(String nativeLibraryName) {
        return new SharedLoader(nativeLibraryName);
    }

    private static void addLibraryPath(final Path path) {
        Platform.addLibraryPath(path);
    }

    private static void removeLibraryPath(final Path path) {
        Platform.removeLibraryPath(path);
    }
}
