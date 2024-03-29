package com.chua.common.support.file.xz;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for filter-specific options classes.
 * @author Administrator
 */
public abstract class AbstractFilterOptions implements Cloneable {
    /**
     * Gets how much memory the encoder will need with
     * the given filter chain. This function simply calls
     * <code>getEncoderMemoryUsage()</code> for every filter
     * in the array and returns the sum of the returned values.
     */
    public static int getEncoderMemoryUsage(AbstractFilterOptions[] options) {
        int m = 0;

        for (int i = 0; i < options.length; ++i) {
            m += options[i].getEncoderMemoryUsage();
        }

        return m;
    }

    /**
     * Gets how much memory the decoder will need with
     * the given filter chain. This function simply calls
     * <code>getDecoderMemoryUsage()</code> for every filter
     * in the array and returns the sum of the returned values.
     */
    public static int getDecoderMemoryUsage(AbstractFilterOptions[] options) {
        int m = 0;

        for (int i = 0; i < options.length; ++i) {
            m += options[i].getDecoderMemoryUsage();
        }

        return m;
    }

    /**
     * Gets how much memory the encoder will need with these options.
     * @return result
     */
    public abstract int getEncoderMemoryUsage();

    /**
     * Gets a raw (no XZ headers) encoder output stream using these options.
     * Raw streams are an advanced feature. In most cases you want to store
     * the compressed data in the .xz container format instead of using
     * a raw stream. To use this filter in a .xz file, pass this object
     * to XZOutputStream.
     * <p>
     * This is uses ArrayCache.getDefaultCache() as the ArrayCache.
     */
    public AbstractFinishableOutputStream getOutputStream(AbstractFinishableOutputStream out) {
        return getOutputStream(out, ArrayCache.getDefaultCache());
    }

    /**
     * Gets a raw (no XZ headers) encoder output stream using these options
     * and the given ArrayCache.
     * Raw streams are an advanced feature. In most cases you want to store
     * the compressed data in the .xz container format instead of using
     * a raw stream. To use this filter in a .xz file, pass this object
     * to XZOutputStream.
     * @param out out
     * @param arrayCache array
     * @return stream
     */
    public abstract AbstractFinishableOutputStream getOutputStream(
            AbstractFinishableOutputStream out, ArrayCache arrayCache);

    /**
     * Gets how much memory the decoder will need to decompress the data
     * that was encoded with these options.
     * @return result
     */
    public abstract int getDecoderMemoryUsage();

    /**
     * Gets a raw (no XZ headers) decoder input stream using these options.
     * <p>
     * This is uses ArrayCache.getDefaultCache() as the ArrayCache.
     */
    public InputStream getInputStream(InputStream in) throws IOException {
        return getInputStream(in, ArrayCache.getDefaultCache());
    }

    /**
     * Gets a raw (no XZ headers) decoder input stream using these options
     * and the given ArrayCache.
     * @param in stream
     * @param arrayCache array
     * @return stream
     * @throws IOException IO
     */
    public abstract InputStream getInputStream(
            InputStream in, ArrayCache arrayCache) throws IOException;

    /**
     * 获取解码器
     * @return 解码器
     */
    abstract FilterEncoder getFilterEncoder();

    AbstractFilterOptions() {}
}
