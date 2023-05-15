package com.chua.common.support.path;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;

/**
 * 删除遍历器
 *
 * @author CH
 * @since 2021-09-29
 */
public class DeletingPathVisitor extends CountingPathVisitor {

    /**
     * Creates a new instance configured with a BigInteger {@link Counters.PathCounters}.
     *
     * @return a new instance configured with a BigInteger {@link Counters.PathCounters}.
     */
    public static DeletingPathVisitor withBigIntegerCounters() {
        return new DeletingPathVisitor(Counters.bigIntegerPathCounters());
    }

    /**
     * Creates a new instance configured with a long {@link Counters.PathCounters}.
     *
     * @return a new instance configured with a long {@link Counters.PathCounters}.
     */
    public static DeletingPathVisitor withLongCounters() {
        return new DeletingPathVisitor(Counters.longPathCounters());
    }

    private String[] skip;
    private boolean overrideReadOnly;
    private LinkOption[] linkOptions;

    /**
     * Constructs a new visitor that deletes files except for the files and directories explicitly given.
     *
     * @param pathCounter How to count visits.
     * @param skip        The files to skip deleting.
     */
    public DeletingPathVisitor(final Counters.PathCounters pathCounter, final String... skip) {
        this(pathCounter, PathUtils.EMPTY_LINK_OPTION_ARRAY, skip);
    }

    /**
     * Constructs a new visitor that deletes files except for the files and directories explicitly given.
     *
     * @param pathCounter How to count visits.
     * @param linkOptions How symbolic links are handled.
     * @param skip        The files to skip deleting.
     * @since 2.9.0
     */
    public DeletingPathVisitor(final Counters.PathCounters pathCounter, final LinkOption[] linkOptions,
                               final String... skip) {
        super(pathCounter);
        final String[] temp = skip != null ? skip.clone() : EMPTY_STRING_ARRAY;
        Arrays.sort(temp);
        this.skip = temp;
        this.overrideReadOnly = true;
        // TODO Files.deleteIfExists() never follows links, so use LinkOption.NOFOLLOW_LINKS in other calls to Files.
        this.linkOptions = linkOptions == null ? PathUtils.NOFOLLOW_LINK_OPTION_ARRAY : linkOptions.clone();
    }

    /**
     * Returns true to process the given path, false if not.
     *
     * @param path the path to test.
     * @return true to process the given path, false if not.
     */
    private boolean accept(final Path path) {
        return Arrays.binarySearch(skip, Objects.toString(path.getFileName(), null)) < 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DeletingPathVisitor other = (DeletingPathVisitor) obj;
        return overrideReadOnly == other.overrideReadOnly && Arrays.equals(skip, other.skip);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(skip);
        result = prime * result + Objects.hash(overrideReadOnly);
        return result;
    }

    @Override
    public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        if (PathUtils.isEmptyDirectory(dir)) {
            Files.deleteIfExists(dir);
        }
        return super.postVisitDirectory(dir, exc);
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        super.preVisitDirectory(dir, attrs);
        return accept(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        if (accept(file)) {
            // delete files and valid links, respecting linkOptions
            if (Files.exists(file, linkOptions)) {
                if (overrideReadOnly) {
                    PathUtils.setReadOnly(file, false, linkOptions);
                }
                Files.deleteIfExists(file);
            }
            // invalid links will survive previous delete, different approach needed:
            if (Files.isSymbolicLink(file)) {
                try {
                    // deleteIfExists does not work for this case
                    Files.delete(file);
                } catch (final NoSuchFileException e) {
                    // ignore
                }
            }
        }
        updateFileCounters(file, attrs);
        return FileVisitResult.CONTINUE;
    }
}
