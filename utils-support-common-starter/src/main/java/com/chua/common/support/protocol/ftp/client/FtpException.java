package com.chua.common.support.protocol.ftp.client;


/**
 * This class helps in represent Ftp error codes and messages.
 *
 * @author Carlo Pelliccia
 */
public class FtpException extends Exception {

	private static final long serialVersionUID = 1L;

	private int code;

	private String message;

	public FtpException(int code) {
		this.code = code;
	}

	public FtpException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public FtpException(FtpReply reply) {
		StringBuffer message = new StringBuffer();
		String[] lines = reply.getMessages();
		for (int i = 0; i < lines.length; i++) {
			if (i > 0) {
				message.append(System.getProperty("line.separator"));
			}
			message.append(lines[i]);
		}
		this.code = reply.getCode();
		this.message = message.toString();
	}

	/**
	 * Returns the code of the occurred Ftp error.
	 *
	 * @return The code of the occurred Ftp error.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns the message of the occurred Ftp error.
	 *
	 * @return The message of the occurred Ftp error.
	 */
	public String getMessage() {
		return message;
	}

	public String toString() {
		return getClass().getName() + " [code=" + code + ", message= "
				+ message + "]";
	}

}
