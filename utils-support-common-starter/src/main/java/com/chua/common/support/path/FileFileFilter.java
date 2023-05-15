package com.chua.common.support.path;

import java.io.File;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * This filter accepts {@code File}s that are files (not directories).
 * <p>
 * For example, here is how to print out a list of the real files
 * within the current directory:
 * </p>
 * <h2>Using Classic IO</h2>
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list(FileFileFilter.INSTANCE);
 * for (String file : files) {
 *     System.out.println(file);
 * }
 * </pre>
 *
 * <h2>Using NIO</h2>
 * <pre>
 * final Path dir = Paths.get("");
 * final AccumulatorPathVisitor visitor = AccumulatorPathVisitor.withLongCounters(FileFileFilter.INSTANCE);
 * //
 * // Walk one dir
 * Files.<b>walkFileTree</b>(dir, Collections.emptySet(), 1, visitor);
 * System.out.println(visitor.getPathCounters());
 * System.out.println(visitor.getFileList());
 * //
 * visitor.getPathCounters().reset();
 * //
 * // Walk dir tree
 * Files.<b>walkFileTree</b>(dir, visitor);
 * System.out.println(visitor.getPathCounters());
 * System.out.println(visitor.getDirList());
 * System.out.println(visitor.getFileList());
 * </pre>
 *
 * @author apache
 * @since 1.3
 */
public class FileFileFilter extends AbstractFileFilter implements Serializable {

    /**
     * Singleton instance of file filter.
     *
     * @since 2.9.0
     */
    public static final IoFileFilter INSTANCE = new FileFileFilter();

    /**
     * Singleton instance of file filter.
     *
     * @deprecated Use {@link #INSTANCE}.
     */
    @Deprecated
    public static final IoFileFilter FILE = INSTANCE;

    private static final long serialVersionUID = 5345244090827540862L;

    /**
     * Restrictive constructor.
     */
    protected FileFileFilter() {
    }

    /**
     * Checks to see if the file is a file.
     *
     * @param file the File to check
     * @return true if the file is a file
     */
    @Override
    public boolean accept(final File file) {
        return file.isFile();
    }

    /**
     * Checks to see if the file is a file.
     *
     * @param file the File to check
     * @return true if the file is a file
     * @since 2.9.0
     */
    @Override
    public FileVisitResult accept(final Path file, final BasicFileAttributes attributes) {
        return toFileVisitResult(Files.isRegularFile(file), file);
    }

}
