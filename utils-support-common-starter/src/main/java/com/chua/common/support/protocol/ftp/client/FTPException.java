package com.chua.common.support.protocol.ftp.client;


/**
 * This class helps in represent FTP error codes and messages.
 *
 * @author Carlo Pelliccia
 */
public class FTPException extends Exception {

	private static final long serialVersionUID = 1L;

	private int code;

	private String message;

	public FTPException(int code) {
		this.code = code;
	}

	public FTPException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public FTPException(FTPReply reply) {
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
	 * Returns the code of the occurred FTP error.
	 *
	 * @return The code of the occurred FTP error.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns the message of the occurred FTP error.
	 *
	 * @return The message of the occurred FTP error.
	 */
	public String getMessage() {
		return message;
	}

	public String toString() {
		return getClass().getName() + " [code=" + code + ", message= "
				+ message + "]";
	}

}
