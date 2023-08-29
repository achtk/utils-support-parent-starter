package com.chua.common.support.protocol.ftp.client;

/**
 * This class represents Ftp server replies in a manageable object oriented way.
 *
 * @author Carlo Pelliccia
 */
public class FtpReply {

	/**
	 * The reply code.
	 */
	private int code = 0;

	/**
	 * The reply message(s).
	 */
	private String[] messages;

	/**
	 * Build the reply.
	 *
	 * @param code    The code of the reply.
	 * @param message The textual message(s) in the reply.
	 */
	FtpReply(int code, String[] messages) {
		this.code = code;
		this.messages = messages;
	}

	/**
	 * Returns the code of the reply.
	 *
	 * @return The code of the reply.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Returns true if the code of the reply is in the range of success codes
	 * (2**).
	 *
	 * @return true if the code of the reply is in the range of success codes
	 * (2**).
	 */
	public boolean isSuccessCode() {
		int aux = code - 200;
		return aux >= 0 && aux < 100;
	}

	/**
	 * Returns the textual message(s) of the reply.
	 *
	 * @return The textual message(s) of the reply.
	 */
	public String[] getMessages() {
		return messages;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName());
		buffer.append(" [code=");
		buffer.append(code);
		buffer.append(", message=");
		for (int i = 0; i < messages.length; i++) {
			if (i > 0) {
				buffer.append(" ");
			}
			buffer.append(messages[i]);
		}
		buffer.append("]");
		return buffer.toString();
	}

}
