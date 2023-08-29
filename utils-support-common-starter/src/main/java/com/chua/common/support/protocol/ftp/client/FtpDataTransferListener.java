package com.chua.common.support.protocol.ftp.client;

/**
 * This interface describes the methods requested by an object that can listen
 * data transfer operations. You can supply an object implementing this
 * interface to any upload/download method of the client.
 *
 * @author Carlo Pelliccia
 */
public interface FtpDataTransferListener {

    /**
     * Called to notify the listener that the transfer operation has been
     * initialized.
     */
    public void started();

    /**
     * Called to notify the listener that some bytes have been transmitted.
     *
     * @param length The number of the bytes transmitted since the last time the
     *               method was called (or since the begin of the operation, at the
     *               first call received).
     */
    public void transferred(int length);

    /**
     * Called to notify the listener that the transfer operation has been
     * successfully complete.
     */
    public void completed();

    /**
     * Called to notify the listener that the transfer operation has been
     * aborted.
     */
    public void aborted();

    /**
     * Called to notify the listener that the transfer operation has failed due
     * to an error.
     */
    public void failed();

}
