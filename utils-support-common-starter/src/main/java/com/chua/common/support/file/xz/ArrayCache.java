package com.chua.common.support.file.xz;

/**
 * Caches large arrays for reuse (base class and a dummy cache implementation).
 * @author ACHTK
 * @since 1.7
 *
 * @see BasicArrayCache
 */
public class ArrayCache {
    /**
     * Global dummy cache instance that is returned by {@code getDummyCache()}.
     */
    private static final ArrayCache DUMMY_CACHE = new ArrayCache();

    /**
     * Global default {@code ArrayCache} that is used when no other cache has
     * been specified.
     */
    private static volatile ArrayCache defaultCache = DUMMY_CACHE;

    /**
     * Returns a statically-allocated {@code ArrayCache} instance.
     * It can be shared by all code that needs a dummy cache.
     */
    public static ArrayCache getDummyCache() {
        return DUMMY_CACHE;
    }

    /**
     * Gets the default {@code ArrayCache} instance.
     * This is a global cache that is used when the application
     * specifies nothing else. The default is a dummy cache
     * (see {@link #getDummyCache()}).
     */
    public static ArrayCache getDefaultCache() {
        // It's volatile so no need for synchronization.
        return defaultCache;
    }

    /**
     * Sets the default {@code ArrayCache} instance.
     * Use with care. Other libraries using this package probably shouldn't
     * call this function as libraries cannot know if there are other users
     * of the xz package in the same application.
     */
    public static void setDefaultCache(ArrayCache arrayCache) {
        if (arrayCache == null) {
            throw new NullPointerException();
        }

        // It's volatile so no need for synchronization.
        defaultCache = arrayCache;
    }

    /**
     * Creates a new {@code ArrayCache} that does no caching
     * (a dummy cache). If you need a dummy cache, you may want to call
     * {@link #getDummyCache()} instead.
     */
    public ArrayCache() {}

    /**
     * Allocates a new byte array.
     * <p>
     * This implementation simply returns {@code new byte[size]}.
     *
     * @param   size            the minimum size of the array to allocate;
     *                          an implementation may return an array that
     *                          is larger than the given {@code size}
     *
     * @param   fillWithZeros   if true, the caller expects that the first
     *                          {@code size} elements in the array are zero;
     *                          if false, the array contents can be anything,
     *                          which speeds things up when reusing a cached
     *                          array
     */
    public byte[] getByteArray(int size, boolean fillWithZeros) {
        return new byte[size];
    }

    /**
     * Puts the given byte array to the cache. The caller must no longer
     * use the array.
     * <p>
     * This implementation does nothing.
     */
    public void putArray(byte[] array) {}

    /**
     * Allocates a new int array.
     * <p>
     * This implementation simply returns {@code new int[size]}.
     *
     * @param   size            the minimum size of the array to allocate;
     *                          an implementation may return an array that
     *                          is larger than the given {@code size}
     *
     * @param   fillWithZeros   if true, the caller expects that the first
     *                          {@code size} elements in the array are zero;
     *                          if false, the array contents can be anything,
     *                          which speeds things up when reusing a cached
     *                          array
     */
    public int[] getIntArray(int size, boolean fillWithZeros) {
        return new int[size];
    }

    /**
     * Puts the given int array to the cache. The caller must no longer
     * use the array.
     * <p>
     * This implementation does nothing.
     */
    public void putArray(int[] array) {}
}
