package com.chua.proxy.support.utils;

import com.chua.proxy.support.buffer.PooledDataBuffer;

import java.awt.image.DataBuffer;

/**
 * Utility class for working with {@link DataBuffer DataBuffers}.
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @since 5.0
 */
public abstract class DataBufferUtils {
    /**
     * Retain the given data buffer, if it is a {@link PooledDataBuffer}.
     * @param dataBuffer the data buffer to retain
     * @return the retained buffer
     */
    @SuppressWarnings("unchecked")
    public static <T extends DataBuffer> T retain(T dataBuffer) {
        if (dataBuffer instanceof PooledDataBuffer) {
            return (T) ((PooledDataBuffer) dataBuffer).retain();
        }
        else {
            return dataBuffer;
        }
    }
}
