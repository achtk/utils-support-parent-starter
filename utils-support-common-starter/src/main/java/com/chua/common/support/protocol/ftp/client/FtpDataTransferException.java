package com.chua.common.support.protocol.ftp.client;

/**
 * Exception thrown if any I/O error occurs during a data transfer attempt.
 *
 * @author Carlo Pelliccia
 */
public class FtpDataTransferException extends Exception {

	private static final long serialVersionUID = 1L;

	public FtpDataTransferException() {
		super();
	}

	public FtpDataTransferException(String message, Throwable cause) {
		super(message, cause);
	}

	public FtpDataTransferException(String message) {
		super(message);
	}

	public FtpDataTransferException(Throwable cause) {
		super(cause);
	}

}
