package com.chua.common.support.protocol.ftp.client;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.NumberConstant.NUM_3;

/**
 * This class is used to represent a communication channel with a Ftp server.
 *
 * @author Carlo Pelliccia
 * @version 1.1
 */
public class FtpCommunicationChannel {

	/**
	 * The FtpCommunicationListener objects registered on the channel.
	 */
	private ArrayList communicationListeners = new ArrayList();

	/**
	 * The connection.
	 */
	private Socket connection = null;

	/**
	 * The name of the charset that has to be used to encode and decode the
	 * communication.
	 */
	private String charsetName = null;

	/**
	 * The stream-reader channel established with the remote server.
	 */
	private NVTASCIIReader reader = null;

	/**
	 * The stream-writer channel established with the remote server.
	 */
	private NVTASCIIWriter writer = null;

	/**
	 * It builds a Ftp communication channel.
	 *
	 * @param connection  The underlying connection.
	 * @param charsetName The name of the charset that has to be used to encode and
	 *                    decode the communication.
	 * @throws IOException If a I/O error occurs.
	 */
	public FtpCommunicationChannel(Socket connection, String charsetName)
			throws IOException {
		this.connection = connection;
		this.charsetName = charsetName;
		InputStream inStream = connection.getInputStream();
		OutputStream outStream = connection.getOutputStream();
		// Wrap the streams into reader and writer objects.
		reader = new NVTASCIIReader(inStream, charsetName);
		writer = new NVTASCIIWriter(outStream, charsetName);
	}

	/**
	 * This method adds a FtpCommunicationListener to the object.
	 *
	 * @param listener The listener.
	 */
	public void addCommunicationListener(FtpCommunicationListener listener) {
		communicationListeners.add(listener);
	}

	/**
	 * This method removes a FtpCommunicationListener previously added to the
	 * object.
	 *
	 * @param listener The listener to be removed.
	 */
	public void removeCommunicationListener(FtpCommunicationListener listener) {
		communicationListeners.remove(listener);
	}

	/**
	 * Closes the channel.
	 */
	public void close() {
		try {
			connection.close();
		} catch (Exception e) {
			;
		}
	}

	/**
	 * This method returns a list with all the FtpCommunicationListener used by
	 * the client.
	 *
	 * @return A list with all the FtpCommunicationListener used by the client.
	 */
	public FtpCommunicationListener[] getCommunicationListeners() {
		int size = communicationListeners.size();
		FtpCommunicationListener[] ret = new FtpCommunicationListener[size];
		for (int i = 0; i < size; i++) {
			ret[i] = (FtpCommunicationListener) communicationListeners.get(i);
		}
		return ret;
	}

	/**
	 * This method reads a line from the remote server.
	 *
	 * @return The string read.
	 * @throws IOException If an I/O error occurs during the operation.
	 */
	private String read() throws IOException {
		// Read the line from the server.
		String line = reader.readLine();
		if (line == null) {
			throw new IOException("FtpConnection closed");
		}
		// Call received() method on every communication listener
		// registered.
		for (Iterator iter = communicationListeners.iterator(); iter.hasNext(); ) {
			FtpCommunicationListener l = (FtpCommunicationListener) iter.next();
			l.received(line);
		}
		// Return the line read.
		return line;
	}

	/**
	 * This method sends a command line to the server.
	 *
	 * @param command The command to be sent.
	 * @throws IOException If an I/O error occurs.
	 */
	public void sendFtpCommand(String command) throws IOException {
		writer.writeLine(command);
		for (Iterator iter = communicationListeners.iterator(); iter.hasNext(); ) {
			FtpCommunicationListener l = (FtpCommunicationListener) iter.next();
			l.sent(command);
		}
	}

	/**
	 * This method reads and parses a Ftp reply statement from the server.
	 *
	 * @return The reply from the server.
	 * @throws IOException              If an I/O error occurs.
	 * @throws FtpIllegalReplyException If the server doesn't reply in a Ftp-compliant way.
	 */
	public FtpReply readFtpReply() throws IOException, FtpIllegalReplyException {
		int code = 0;
		ArrayList messages = new ArrayList();
		do {
			String statement;
			do {
				statement = read();
			} while (statement.trim().length() == 0);
			if (statement.startsWith(SYMBOL_N)) {
				statement = statement.substring(1);
			}
			int l = statement.length();
			if (code == 0 && l < NUM_3) {
				throw new FtpIllegalReplyException();
			}
			int aux;
			try {
				aux = Integer.parseInt(statement.substring(0, 3));
			} catch (Exception e) {
				if (code == 0) {
					throw new FtpIllegalReplyException();
				} else {
					aux = 0;
				}
			}
			if (code != 0 && aux != 0 && aux != code) {
				throw new FtpIllegalReplyException();
			}
			if (code == 0) {
				code = aux;
			}
			if (aux > 0) {
				if (l > NUM_3) {
					char s = statement.charAt(3);
					messages.add(statement.substring(4, l));
					if (s == SYMBOL_BLANK_CHAR) {
						break;
					} else if (s == SYMBOL_MINUS_CHAR) {
						continue;
					} else {
						throw new FtpIllegalReplyException();
					}
				} else if (l == NUM_3) {
					break;
				} else {
					messages.add(statement);
				}
			} else {
				messages.add(statement);
			}
		} while (true);
		int size = messages.size();
		String[] m = new String[size];
		for (int i = 0; i < size; i++) {
			m[i] = (String) messages.get(i);
		}
		return new FtpReply(code, m);
	}

	/**
	 * Changes the current charset.
	 *
	 * @param charsetName The new charset.
	 * @throws IOException If I/O error occurs.
	 * @since 1.1
	 */
	public void changeCharset(String charsetName) throws IOException {
		this.charsetName = charsetName;
		reader.changeCharset(charsetName);
		writer.changeCharset(charsetName);
	}

	/**
	 * Applies SSL encryption to the communication channel.
	 *
	 * @param sslSocketFactory The SSLSocketFactory used to produce the SSL connection.
	 * @throws IOException If a I/O error occurs.
	 * @since 1.4
	 */
	public void ssl(SSLSocketFactory sslSocketFactory) throws IOException {
		String host = connection.getInetAddress().getHostName();
		int port = connection.getPort();
		connection = sslSocketFactory.createSocket(connection, host, port, true);
		InputStream inStream = connection.getInputStream();
		OutputStream outStream = connection.getOutputStream();
		reader = new NVTASCIIReader(inStream, charsetName);
		writer = new NVTASCIIWriter(outStream, charsetName);
	}

}
