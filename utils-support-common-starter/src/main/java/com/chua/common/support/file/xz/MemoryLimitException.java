/*
 * MemoryLimitException
 *
 * Author: Lasse Collin <lasse.collin@tukaani.org>
 *
 * This file has been put into the public domain.
 * You can do whatever you want with this file.
 */

package com.chua.common.support.file.xz;

/**
 * Thrown when the memory usage limit given to the Xz decompressor
 * would be exceeded.
 * <p>
 * The amount of memory required and the memory usage limit are
 * included in the error detail message in human readable format.
 *
 * @author Administrator
 */
public class MemoryLimitException extends XzException {
    private static final long serialVersionUID = 3L;

    private final int memoryNeeded;
    private final int memoryLimit;

    /**
     * Creates a new MemoryLimitException.
     * <p>
     * The amount of memory needed and the memory usage limit are
     * included in the error detail message.
     *
     * @param   memoryNeeded    amount of memory needed as kibibytes (KiB)
     * @param   memoryLimit     specified memory usage limit as kibibytes (KiB)
     */
    public MemoryLimitException(int memoryNeeded, int memoryLimit) {
        super("" + memoryNeeded + " KiB of memory would be needed; limit was "
              + memoryLimit + " KiB");

        this.memoryNeeded = memoryNeeded;
        this.memoryLimit = memoryLimit;
    }

    /**
     * Gets how much memory is required to decompress the data.
     *
     * @return      amount of memory needed as kibibytes (KiB)
     */
    public int getMemoryNeeded() {
        return memoryNeeded;
    }

    /**
     * Gets what the memory usage limit was at the time the exception
     * was created.
     *
     * @return      memory usage limit as kibibytes (KiB)
     */
    public int getMemoryLimit() {
        return memoryLimit;
    }
}
