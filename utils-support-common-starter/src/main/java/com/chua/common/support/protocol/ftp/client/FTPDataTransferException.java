package com.chua.common.support.protocol.ftp.client;

/**
 * Exception thrown if any I/O error occurs during a data transfer attempt.
 *
 * @author Carlo Pelliccia
 */
public class FTPDataTransferException extends Exception {

	private static final long serialVersionUID = 1L;

	public FTPDataTransferException() {
		super();
	}

	public FTPDataTransferException(String message, Throwable cause) {
		super(message, cause);
	}

	public FTPDataTransferException(String message) {
		super(message);
	}

	public FTPDataTransferException(Throwable cause) {
		super(cause);
	}

}
