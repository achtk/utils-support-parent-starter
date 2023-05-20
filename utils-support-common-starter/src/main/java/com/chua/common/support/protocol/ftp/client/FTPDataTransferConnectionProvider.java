package com.chua.common.support.protocol.ftp.client;

import java.net.Socket;

/**
 * A package reserved {@link com.chua.common.support.protocol.ftp.server.FtpConnection} provider, used internally by the
 * client to obtain connections for data transfer purposes.
 *
 * @author cpelliccia
 */
interface FTPDataTransferConnectionProvider {

	/**
	 * Returns the connection.
	 *
	 * @return The connection.
	 * @throws FTPException If an unexpected error occurs.
	 */
	public Socket openDataTransferConnection() throws FTPDataTransferException;

	/**
	 * Terminates the provider.
	 */
	public void dispose();

}
